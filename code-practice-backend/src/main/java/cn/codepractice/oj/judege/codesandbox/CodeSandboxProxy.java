package cn.codepractice.oj.judege.codesandbox;

import cn.codepractice.oj.judege.codesandbox.model.ExecuteCodeRequest;
import cn.codepractice.oj.judege.codesandbox.model.ExecuteCodeResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class CodeSandboxProxy implements CodeSandbox {

    private CodeSandbox codeSandbox;

    @Override
    public ExecuteCodeResponse doExecute(ExecuteCodeRequest executeCodeRequest) {
        log.info("Code sandbox request: {}", executeCodeRequest);
        ExecuteCodeResponse executeCodeResponse = codeSandbox.doExecute(executeCodeRequest);
        log.info("Code sandbox response: {}", executeCodeResponse);
        return executeCodeResponse;
    }
}
