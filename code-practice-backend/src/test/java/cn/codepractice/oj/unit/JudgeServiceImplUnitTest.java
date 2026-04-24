package cn.codepractice.oj.unit;

import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.judege.strategy.JudgeManager;
import cn.codepractice.oj.judege.JudgeServiceImpl;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.entity.QuestionSubmit;
import cn.codepractice.oj.model.enums.QuestionSubmitStatusEnum;
import cn.codepractice.oj.service.QuestionService;
import cn.codepractice.oj.service.QuestionSubmitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Модульные проверки ранних отказов в сервисе проверки решений.
 */
@ExtendWith(MockitoExtension.class)
class JudgeServiceImplUnitTest {

    @Mock
    private QuestionService questionService;
    @Mock
    private QuestionSubmitService questionSubmitService;
    @Mock
    private JudgeManager judgeManager;

    private JudgeServiceImpl judgeService;

    @BeforeEach
    void setUp() {
        judgeService = new JudgeServiceImpl();
        ReflectionTestUtils.setField(judgeService, "questionService", questionService);
        ReflectionTestUtils.setField(judgeService, "questionSubmitService", questionSubmitService);
        ReflectionTestUtils.setField(judgeService, "judgeManager", judgeManager);
        ReflectionTestUtils.setField(judgeService, "type", "example");
    }

    @Test
    void doJudge_submitMissing_throws() {
        when(questionSubmitService.getById(1L)).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class, () -> judgeService.doJudge(1L));
        assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
    }

    @Test
    void doJudge_questionMissing_throws() {
        QuestionSubmit s = new QuestionSubmit();
        s.setQuestionId(2L);
        s.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        when(questionSubmitService.getById(1L)).thenReturn(s);
        when(questionService.getById(2L)).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class, () -> judgeService.doJudge(1L));
        assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
    }

    @Test
    void doJudge_wrongStatus_throws() {
        QuestionSubmit s = new QuestionSubmit();
        s.setQuestionId(2L);
        s.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        when(questionSubmitService.getById(1L)).thenReturn(s);
        when(questionService.getById(2L)).thenReturn(new Question());
        BusinessException ex = assertThrows(BusinessException.class, () -> judgeService.doJudge(1L));
        assertEquals(ErrorCode.OPERATION_ERROR.getCode(), ex.getCode());
    }
}
