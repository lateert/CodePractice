package cn.codepractice.oj.unit;

import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.service.impl.QuestionServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Модульные проверки валидации сущности «задача» (бизнес-правила без БД и Spring).
 */
class QuestionServiceImplValidQuestionTest {

    private final QuestionServiceImpl questionService = new QuestionServiceImpl();

    @Test
    void validQuestion_null_throwsParamsError() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> questionService.validQuestion(null, true));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
    }

    @Test
    void validQuestion_add_missingRequired_throws() {
        Question q = new Question();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> questionService.validQuestion(q, true));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
    }

    @Test
    void validQuestion_add_ok_minimal() {
        Question q = new Question();
        q.setTitle("T");
        q.setContent("C");
        q.setTags("[]");
        assertDoesNotThrow(() -> questionService.validQuestion(q, true));
    }

    @Test
    void validQuestion_titleTooLong_throws() {
        Question q = new Question();
        q.setTitle(StringUtils.repeat("a", 81));
        q.setContent("c");
        q.setTags("[]");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> questionService.validQuestion(q, true));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
    }

    @Test
    void validQuestion_contentTooLong_throws() {
        Question q = new Question();
        q.setTitle("ok");
        q.setContent(StringUtils.repeat("x", 8193));
        q.setTags("[]");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> questionService.validQuestion(q, false));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
    }

    @Test
    void validQuestion_update_blankFieldsAllowed() {
        Question q = new Question();
        assertDoesNotThrow(() -> questionService.validQuestion(q, false));
    }
}
