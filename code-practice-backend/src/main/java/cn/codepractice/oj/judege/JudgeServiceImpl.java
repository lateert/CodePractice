package cn.codepractice.oj.judege;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.json.JSONUtil;
import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.judege.codesandbox.CodeSandbox;
import cn.codepractice.oj.judege.codesandbox.CodeSandboxFactory;
import cn.codepractice.oj.judege.codesandbox.CodeSandboxProxy;
import cn.codepractice.oj.judege.codesandbox.model.ExecuteCodeRequest;
import cn.codepractice.oj.judege.codesandbox.model.ExecuteCodeResponse;
import cn.codepractice.oj.judege.strategy.JudgeContext;
import cn.codepractice.oj.judege.strategy.JudgeManager;
import cn.codepractice.oj.model.dto.question.JudgeCase;
import cn.codepractice.oj.judege.codesandbox.model.JudgeInfo;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.entity.QuestionSubmit;
import cn.codepractice.oj.model.enums.QuestionSubmitStatusEnum;
import cn.codepractice.oj.service.QuestionService;
import cn.codepractice.oj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * Сервис проверки отправленных решений (вызов песочницы и стратегий сравнения).
 */
@Service
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {

        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (null == questionSubmit) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Отправка решения не найдена");
        }

        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (null == question) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Задача не найдена");
        }

        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Проверка уже выполняется или завершена");
        }

        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Не удалось обновить статус отправки");
        }


        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);

        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);

        codeSandbox = new CodeSandboxProxy(codeSandbox);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .input(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.doExecute(executeCodeRequest);

        Integer status = executeCodeResponse.getStatus();

        if (status == null) {
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.COMPLICATE_FAILED.getValue());
            update = questionSubmitService.updateById(questionSubmitUpdate);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Не удалось обновить статус отправки");
            }
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Ошибка компиляции или выполнения");
        }

        List<String> outputList = executeCodeResponse.getOutput();

        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setQuestion(question);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestionSubmit(questionSubmit);

        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(status);
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Не удалось сохранить результат проверки");
        }

        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionSubmitId);
        return questionSubmitResult;
    }
}
