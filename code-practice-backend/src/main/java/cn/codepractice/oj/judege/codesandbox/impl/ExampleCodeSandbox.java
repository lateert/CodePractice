package cn.codepractice.oj.judege.codesandbox.impl;
import java.util.List;

import cn.codepractice.oj.judege.codesandbox.CodeSandbox;
import cn.codepractice.oj.judege.codesandbox.model.ExecuteCodeRequest;
import cn.codepractice.oj.judege.codesandbox.model.ExecuteCodeResponse;
import cn.codepractice.oj.judege.codesandbox.model.JudgeInfo;
import cn.codepractice.oj.model.enums.JudgeInfoMessageEnum;
import cn.codepractice.oj.model.enums.QuestionSubmitStatusEnum;

/**、
 * 
 * @author peiYP
 * @create 2023-12-31 18:08
 **/
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse doExecute(ExecuteCodeRequest executeCodeRequest) {
        List<String> input = executeCodeRequest.getInput();

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setMessage("OK");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        executeCodeResponse.setOutput(input);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);

        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
