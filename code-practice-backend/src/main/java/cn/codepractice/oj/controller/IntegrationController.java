package cn.codepractice.oj.controller;

import cn.codepractice.oj.common.BaseResponse;
import cn.codepractice.oj.common.ResultUtils;
import cn.codepractice.oj.integration.GithubOAuthService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/integration", "/v1/integration"})
public class IntegrationController {

    @Resource
    private GithubOAuthService githubOAuthService;

    /**
     * Внешняя REST-интеграция #2: GitHub Search API.
     */
    @GetMapping("/github/repositories/search")
    public BaseResponse<String> searchGithubRepositories(@RequestParam(required = false) String q,
                                                         @RequestParam(defaultValue = "5") int limit) {
        return ResultUtils.success(githubOAuthService.searchPublicRepositories(q, limit));
    }
}
