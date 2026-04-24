package cn.codepractice.oj.judege;

import cn.codepractice.oj.model.entity.QuestionSubmit;

public interface JudgeService {

    /**
     * @param questionSubmitId submission id
     * @return updated submission entity after judging
     */
    QuestionSubmit doJudge(long questionSubmitId);

}
