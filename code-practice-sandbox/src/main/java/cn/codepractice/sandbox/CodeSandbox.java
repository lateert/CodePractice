package cn.codepractice.sandbox;


import cn.codepractice.sandbox.model.ExecuteCodeRequest;
import cn.codepractice.sandbox.model.ExecuteCodeResponse;

/** Выполнение пользовательского кода в изолированной среде. */
public interface CodeSandbox {

    ExecuteCodeResponse doExecute(ExecuteCodeRequest executeCodeRequest);
}

