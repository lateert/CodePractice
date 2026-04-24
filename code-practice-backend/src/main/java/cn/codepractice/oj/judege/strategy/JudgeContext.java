package cn.codepractice.oj.judege.strategy;

import cn.codepractice.oj.model.dto.question.JudgeCase;
import cn.codepractice.oj.judege.codesandbox.model.JudgeInfo;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private Question question;

    private List<JudgeCase> judgeCaseList;

    private QuestionSubmit questionSubmit;

}
