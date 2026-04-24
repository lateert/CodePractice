package cn.codepractice.sandbox;


import cn.codepractice.sandbox.model.ExecuteCodeRequest;
import cn.codepractice.sandbox.model.ExecuteCodeResponse;

/**
 * @author peiYP
 * @create 2023-12-31 17:54
 **/
public interface CodeSandbox {

    ExecuteCodeResponse doExecute(ExecuteCodeRequest executeCodeRequest);
}

