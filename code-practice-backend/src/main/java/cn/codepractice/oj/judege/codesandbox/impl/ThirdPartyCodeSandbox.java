package cn.codepractice.oj.judege.codesandbox.impl;

import cn.codepractice.oj.judege.codesandbox.CodeSandbox;
import cn.codepractice.oj.judege.codesandbox.model.ExecuteCodeRequest;
import cn.codepractice.oj.judege.codesandbox.model.ExecuteCodeResponse;


public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse doExecute(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("ThirdPartyCodeSandboxImpl.doExecute");
        return null;
    }
}
