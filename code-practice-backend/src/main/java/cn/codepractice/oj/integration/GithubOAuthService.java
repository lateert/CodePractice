package cn.codepractice.oj.integration;

import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.config.GithubOAuthProperties;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class GithubOAuthService {

    private static final String PROVIDER = "github";
    private static final String AUTHORIZE_URL = "https://github.com/login/oauth/authorize";
    private static final String TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String USER_URL = "https://api.github.com/user";

    @Resource
    private GithubOAuthProperties githubOAuthProperties;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ExternalAccountRepository externalAccountRepository;

    @Resource
    private UserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    public String buildLoginUrl(String state) {
        validateOauthConfigured();
        return UriComponentsBuilder.fromHttpUrl(AUTHORIZE_URL)
                .queryParam("client_id", githubOAuthProperties.getClientId())
                .queryParam("redirect_uri", githubOAuthProperties.getRedirectUri())
                .queryParam("scope", "read:user user:email")
                .queryParam("state", StringUtils.defaultIfBlank(state, "code-practice"))
                .build()
                .toUriString();
    }

    public User loginByAuthorizationCode(String code) {
        validateOauthConfigured();
        if (StringUtils.isBlank(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "code обязателен");
        }
        String accessToken = exchangeCodeForAccessToken(code);
        GithubUserInfo userInfo = fetchGithubUser(accessToken);
        if (StringUtils.isBlank(userInfo.id) || StringUtils.isBlank(userInfo.login)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Не удалось получить данные профиля GitHub");
        }
        Long userId = externalAccountRepository.findUserIdByProviderAndExternalId(PROVIDER, userInfo.id);
        if (userId != null) {
            User user = userService.getById(userId);
            if (user != null) {
                return user;
            }
        }
        User created = createUserFromGithub(userInfo);
        externalAccountRepository.bind(PROVIDER, userInfo.id, created.getId(), accessToken, null);
        return created;
    }

    public String searchPublicRepositories(String query, int limit) {
        String searchQuery = StringUtils.defaultIfBlank(query, "java");
        int safeLimit = Math.max(1, Math.min(limit, 20));
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://api.github.com/search/repositories")
                .queryParam("q", searchQuery)
                .queryParam("sort", "stars")
                .queryParam("order", "desc")
                .queryParam("per_page", safeLimit)
                .build(true)
                .toUri();
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "GitHub search API недоступен");
        }
        return response.getBody();
    }

    private String exchangeCodeForAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        Map<String, String> body = new HashMap<>();
        body.put("client_id", githubOAuthProperties.getClientId());
        body.put("client_secret", githubOAuthProperties.getClientSecret());
        body.put("code", code);
        body.put("redirect_uri", githubOAuthProperties.getRedirectUri());
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URL, entity, String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "Ошибка получения GitHub access token");
        }
        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            String accessToken = json.path("access_token").asText(null);
            if (StringUtils.isBlank(accessToken)) {
                throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "GitHub не вернул access token");
            }
            return accessToken;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "Некорректный ответ GitHub token API");
        }
    }

    private GithubUserInfo fetchGithubUser(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(USER_URL, HttpMethod.GET, entity, String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "Ошибка получения профиля GitHub");
        }
        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            GithubUserInfo info = new GithubUserInfo();
            info.id = json.path("id").asText();
            info.login = json.path("login").asText();
            info.name = readOptionalGithubText(json.path("name"));
            info.avatarUrl = json.path("avatar_url").asText();
            return info;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "Некорректный ответ GitHub user API");
        }
    }

    private User createUserFromGithub(GithubUserInfo userInfo) {
        String baseAccount = "gh_" + userInfo.login.toLowerCase();
        String account = baseAccount;
        int counter = 0;
        while (existsAccount(account)) {
            counter++;
            account = baseAccount + "_" + counter;
        }
        User user = new User();
        user.setUserAccount(account);
        user.setUserName(StringUtils.defaultIfBlank(
                normalizeGithubDisplayName(userInfo.name),
                userInfo.login));
        user.setUserAvatar(userInfo.avatarUrl);
        user.setUserRole("user");
        user.setUserPassword(passwordEncoder.encode(RandomStringUtils.randomAlphanumeric(24)));
        boolean saved = userService.save(user);
        if (!saved) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Не удалось создать пользователя GitHub");
        }
        return user;
    }

    private boolean existsAccount(String account) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", account);
        return userService.count(queryWrapper) > 0;
    }

    /** Пусто, если в JSON было null / отсутствует / пустая строка (не строка {@code "null"}). */
    private static String readOptionalGithubText(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        String t = node.asText();
        return StringUtils.trimToNull(t);
    }

    private static String normalizeGithubDisplayName(String name) {
        if (name == null) {
            return null;
        }
        String t = name.trim();
        if (t.isEmpty() || "null".equalsIgnoreCase(t)) {
            return null;
        }
        return t;
    }

    private void validateOauthConfigured() {
        if (StringUtils.isAnyBlank(
                githubOAuthProperties.getClientId(),
                githubOAuthProperties.getClientSecret(),
                githubOAuthProperties.getRedirectUri()
        )) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "GitHub OAuth не настроен");
        }
    }

    private static class GithubUserInfo {
        private String id;
        private String login;
        private String name;
        private String avatarUrl;
    }
}
