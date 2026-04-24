package cn.codepractice.oj.judege.strategy;

import cn.codepractice.oj.judege.codesandbox.model.JudgeInfo;

public interface JudgeStrategy {

    JudgeInfo doJudge(JudgeContext judgeContext);

}
