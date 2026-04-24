package cn.codepractice.oj.judege.codesandbox;

import cn.codepractice.oj.judege.codesandbox.model.ExecuteCodeRequest;
import cn.codepractice.oj.judege.codesandbox.model.ExecuteCodeResponse;

/**
 * @author peiYP
 * @create 2023-12-31 17:54
 **/
public interface CodeSandbox {

    ExecuteCodeResponse doExecute(ExecuteCodeRequest executeCodeRequest);


}

