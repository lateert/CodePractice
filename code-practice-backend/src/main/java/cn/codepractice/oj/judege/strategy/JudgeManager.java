package cn.codepractice.oj.judege.strategy;

import cn.codepractice.oj.judege.codesandbox.model.JudgeInfo;
import cn.codepractice.oj.model.enums.QuestionSubmitLanguageEnum;
import org.springframework.stereotype.Service;

@Service
public class JudgeManager {

    public JudgeInfo doJudge(JudgeContext judgeContext) {
        String language = judgeContext.getQuestionSubmit().getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if (QuestionSubmitLanguageEnum.JAVA.getValue().equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
