package cn.codepractice.sandbox;

import cn.codepractice.sandbox.model.ExecuteCodeRequest;
import cn.codepractice.sandbox.model.ExecuteCodeResponse;
import cn.codepractice.sandbox.model.ExecuteMessage;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class JavaNativeCodeSandbox extends CodeSandboxTemplate {

    @Override
    protected File saveCodeToFile(String code) {
        return super.saveCodeToFile(code);
    }

    @Override
    protected ExecuteMessage compileFile(File userCodeFile) {
        return super.compileFile(userCodeFile);
    }

    @Override
    protected List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        return super.runFile(userCodeFile, inputList);
    }

    @Override
    protected ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        return super.getOutputResponse(executeMessageList);
    }

    @Override
    protected boolean deleteFile(File userCodeFile) {
        return super.deleteFile(userCodeFile);
    }

    @Override
    public ExecuteCodeResponse doExecute(ExecuteCodeRequest executeCodeRequest) {
        return super.doExecute(executeCodeRequest);
    }
}
