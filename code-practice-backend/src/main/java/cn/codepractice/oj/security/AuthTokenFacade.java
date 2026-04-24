package cn.codepractice.oj.security;

import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.config.AuthJwtProperties;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class AuthTokenFacade {

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private RefreshTokenRepository refreshTokenRepository;

    @Resource
    private AuthJwtProperties authJwtProperties;

    @Resource
    private UserService userService;

    public void issueTokens(User user, HttpServletResponse response) {
        JwtTokenPair pair = jwtTokenService.createTokenPair(user);
        refreshTokenRepository.save(
                user.getId(),
                pair.getRefreshTokenId(),
                Instant.now().plus(Duration.ofDays(authJwtProperties.getRefreshTokenDays()))
        );
        writeAccessCookie(response, pair.getAccessToken());
        writeRefreshCookie(response, pair.getRefreshToken());
    }

    public User refresh(HttpServletRequest request, HttpServletResponse response, String refreshTokenFromBody) {
        String refreshToken = StringUtils.defaultIfBlank(refreshTokenFromBody, readCookie(request, authJwtProperties.getRefreshCookieName()));
        if (StringUtils.isBlank(refreshToken)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "Refresh token отсутствует");
        }

        JwtTokenPayload payload = jwtTokenService.parseRefreshToken(refreshToken);
        boolean validStoredToken = refreshTokenRepository.isValid(payload.getUserId(), payload.getTokenId());
        if (!validStoredToken) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "Refresh token отозван или просрочен");
        }

        User user = userService.getById(payload.getUserId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Пользователь не найден");
        }

        refreshTokenRepository.revoke(payload.getTokenId());
        issueTokens(user, response);
        refreshTokenRepository.cleanupExpired();
        return user;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = readCookie(request, authJwtProperties.getRefreshCookieName());
        if (StringUtils.isNotBlank(refreshToken)) {
            try {
                JwtTokenPayload payload = jwtTokenService.parseRefreshToken(refreshToken);
                refreshTokenRepository.revoke(payload.getTokenId());
            } catch (Exception ignored) {
                // invalid/expired token on logout is safe to ignore
            }
        }
        clearCookies(response);
    }

    public String readAccessToken(HttpServletRequest request) {
        return readCookie(request, authJwtProperties.getAccessCookieName());
    }

    private void writeAccessCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(authJwtProperties.getAccessCookieName(), token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMinutes(authJwtProperties.getAccessTokenMinutes()))
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void writeRefreshCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(authJwtProperties.getRefreshCookieName(), token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(authJwtProperties.getRefreshTokenDays()))
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void clearCookies(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from(authJwtProperties.getAccessCookieName(), "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from(authJwtProperties.getRefreshCookieName(), "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    private static String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
