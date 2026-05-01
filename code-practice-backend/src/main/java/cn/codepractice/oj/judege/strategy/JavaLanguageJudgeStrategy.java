package cn.codepractice.oj.judege.strategy;

import cn.hutool.json.JSONUtil;
import cn.codepractice.oj.model.dto.question.JudgeCase;
import cn.codepractice.oj.model.dto.question.JudgeConfig;
import cn.codepractice.oj.judege.codesandbox.model.JudgeInfo;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.enums.JudgeInfoMessageEnum;
import cn.codepractice.oj.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

public class JavaLanguageJudgeStrategy implements JudgeStrategy {
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
        //  (песочница должна вернуть по одному выводу на каждый вход)
        if (outputList.size() != inputList.size()) {
            messageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            String detail = String.format(" (выводов: %d, ожидалось тестов: %d)",
                    outputList.size(), inputList.size());
            judgeInfoResponse.setMessage(messageEnum.getValue() + detail);
            judgeInfoResponse.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            return judgeInfoResponse;
        }

        // Нормализация вывода: убираем \r, приводим к одной строке, trim
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            String expected = normalizeOutput(judgeCase.getOutput());
            String actual = i < outputList.size() && outputList.get(i) != null
                    ? normalizeOutput(outputList.get(i)) : "";
            if (!expected.equals(actual)) {
                messageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                String detail = String.format(" (тест %d: ожидалось «%s», получено «%s»)",
                        i + 1, truncate(expected, 50), truncate(actual, 50));
                judgeInfoResponse.setMessage(messageEnum.getValue() + detail);
                judgeInfoResponse.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
                return judgeInfoResponse;
            }
        }

        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        if (memory > needMemoryLimit * 1024 * 1024) {
            messageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(messageEnum.getValue());
            judgeInfoResponse.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            return judgeInfoResponse;
        }

        if (time > needTimeLimit) {
            messageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(messageEnum.getValue());
            judgeInfoResponse.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            return judgeInfoResponse;
        }

        judgeInfoResponse.setMessage(messageEnum.getValue());
        judgeInfoResponse.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        return judgeInfoResponse;
    }

    /** Нормализация вывода: `\r\n` и `\r` в `\n`, затем обрезка краевых пробелов. */
    private static String normalizeOutput(String s) {
        if (s == null) return "";
        return s.replace("\r\n", "\n").replace("\r", "\n").trim();
    }

    private static String truncate(String s, int maxLen) {
        if (s == null) return "";
        s = s.replace("\n", "\\n").replace("\r", "\\r");
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
}
