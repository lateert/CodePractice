package cn.codepractice.oj.judege.codesandbox.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.judege.codesandbox.CodeSandbox;
import cn.codepractice.oj.judege.codesandbox.model.ExecuteCodeRequest;
import cn.codepractice.oj.judege.codesandbox.model.ExecuteCodeResponse;

/**
 * 
 * @author peiYP
 * @create 2023-12-31 18:08
 **/
public class RemoteCodeSandbox implements CodeSandbox {

    // 
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String DEFAULT_AUTH_REQUEST_SECRET = "secretKey";

    @Override
    public ExecuteCodeResponse doExecute(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("RemoteCodeSandboxImpl.doExecute");
        String url = "http://localhost:8220/executeCode";
        String authSecret = System.getenv("SANDBOX_AUTH_SECRET");
        if (StrUtil.isBlank(authSecret)) {
            authSecret = DEFAULT_AUTH_REQUEST_SECRET;
        }
        String jsonStr = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, authSecret)
                .body(jsonStr)
                .execute()
                .body();

        if (StrUtil.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "message=" + responseStr);
        }

        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
