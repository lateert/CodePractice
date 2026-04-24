package cn.codepractice.oj.controller;

import cn.codepractice.oj.common.BaseResponse;
import cn.codepractice.oj.common.ResultUtils;
import cn.codepractice.oj.integration.GithubOAuthService;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.model.vo.LoginUserVO;
import cn.codepractice.oj.security.AuthTokenFacade;
import cn.codepractice.oj.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/oauth", "/v1/oauth"})
public class OAuthController {

    @Resource
    private GithubOAuthService githubOAuthService;

    @Resource
    private AuthTokenFacade authTokenFacade;

    @Resource
    private UserService userService;

    @GetMapping("/github/login-url")
    public BaseResponse<String> githubLoginUrl(@RequestParam(required = false) String state) {
        return ResultUtils.success(githubOAuthService.buildLoginUrl(state));
    }

    @GetMapping("/github/callback")
    public BaseResponse<LoginUserVO> githubCallback(@RequestParam String code,
                                                    @RequestParam(required = false) String state,
                                                    HttpServletResponse response) {
        User user = githubOAuthService.loginByAuthorizationCode(code);
        authTokenFacade.issueTokens(user, response);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }
}
