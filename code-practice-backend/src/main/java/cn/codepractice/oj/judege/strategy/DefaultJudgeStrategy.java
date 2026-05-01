package cn.codepractice.oj.judege.strategy;
import java.util.List;
import cn.codepractice.oj.model.entity.Question;

import cn.hutool.json.JSONUtil;
import cn.codepractice.oj.model.dto.question.JudgeCase;
import cn.codepractice.oj.model.dto.question.JudgeConfig;
import cn.codepractice.oj.judege.codesandbox.model.JudgeInfo;
import cn.codepractice.oj.model.enums.JudgeInfoMessageEnum;

public class DefaultJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long memory = judgeInfo.getMemory();
        Long time = judgeInfo.getTime();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();

        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setTime(time);
        judgeInfoResponse.setMemory(memory);


        JudgeInfoMessageEnum messageEnum = JudgeInfoMessageEnum.ACCEPTED;
        // 
        if (outputList.size() != inputList.size()) {
            messageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(messageEnum.getValue());
            return judgeInfoResponse;
        }

        //  (нормализуем пробелы и переводы строк в конце)
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            String expected = judgeCase.getOutput() != null ? judgeCase.getOutput().trim() : "";
            String actual = i < outputList.size() && outputList.get(i) != null ? outputList.get(i).trim() : "";
            if (!expected.equals(actual)) {
                messageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(messageEnum.getValue());
                return judgeInfoResponse;
            }
        }

        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        if (memory > needMemoryLimit) {
            messageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(messageEnum.getValue());
            return judgeInfoResponse;
        }

        if (time > needTimeLimit) {
            messageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(messageEnum.getValue());
            return judgeInfoResponse;
        }

        judgeInfoResponse.setMessage(messageEnum.getValue());
        return judgeInfoResponse;
    }
}
