package cn.codepractice.oj.unit;

import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.judege.JudgeService;
import cn.codepractice.oj.mapper.QuestionSubmitMapper;
import cn.codepractice.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import cn.codepractice.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import cn.codepractice.oj.model.entity.CourseQuestion;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.entity.QuestionSubmit;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.service.CourseQuestionService;
import cn.codepractice.oj.service.CourseService;
import cn.codepractice.oj.service.QuestionService;
import cn.codepractice.oj.service.UserService;
import cn.codepractice.oj.service.impl.QuestionSubmitServiceImpl;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Модульные проверки отправки решения и построения {@link QueryWrapper} (Mockito).
 */
@ExtendWith(MockitoExtension.class)
class QuestionSubmitServiceImplUnitTest {

    private QuestionSubmitServiceImpl service;
    private QuestionService questionService;
    private UserService userService;
    private CourseQuestionService courseQuestionService;
    private CourseService courseService;
    private JudgeService judgeService;
    private QuestionSubmitMapper questionSubmitMapper;

    @BeforeEach
    void setUp() {
        questionService = mock(QuestionService.class);
        userService = mock(UserService.class);
        courseQuestionService = mock(CourseQuestionService.class);
        courseService = mock(CourseService.class);
        judgeService = mock(JudgeService.class);
        questionSubmitMapper = mock(QuestionSubmitMapper.class);

        service = spy(new QuestionSubmitServiceImpl());
        ReflectionTestUtils.setField(service, "questionService", questionService);
        ReflectionTestUtils.setField(service, "userService", userService);
        ReflectionTestUtils.setField(service, "courseQuestionService", courseQuestionService);
        ReflectionTestUtils.setField(service, "courseService", courseService);
        ReflectionTestUtils.setField(service, "judgeService", judgeService);
        ReflectionTestUtils.setField(service, "baseMapper", questionSubmitMapper);
    }

    @Test
    void doQuestionSubmit_unsupportedLanguage_throws() {
        QuestionSubmitAddRequest req = new QuestionSubmitAddRequest();
        req.setLanguage("brainfuck");
        req.setQuestionId(1L);
        req.setCode("x");
        User u = new User();
        u.setId(10L);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.doQuestionSubmit(req, u));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
    }

    @Test
    void doQuestionSubmit_questionMissing_throws() {
        QuestionSubmitAddRequest req = new QuestionSubmitAddRequest();
        req.setLanguage("java");
        req.setQuestionId(99L);
        req.setCode("class Main {}");
        when(questionService.getById(99L)).thenReturn(null);
        User u = new User();
        u.setId(10L);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.doQuestionSubmit(req, u));
        assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
    }

    @Test
    void doQuestionSubmit_success_returnsIdAndStartsJudge() {
        QuestionSubmitAddRequest req = new QuestionSubmitAddRequest();
        req.setLanguage("java");
        req.setQuestionId(1L);
        req.setCode("class Main {}");
        Question q = new Question();
        q.setId(1L);
        when(questionService.getById(1L)).thenReturn(q);
        doAnswer(invocation -> {
            QuestionSubmit s = invocation.getArgument(0);
            s.setId(555L);
            return true;
        }).when(service).save(any(QuestionSubmit.class));

        User u = new User();
        u.setId(10L);
        long id = service.doQuestionSubmit(req, u);
        assertEquals(555L, id);
        verify(judgeService).doJudge(555L);
    }

    @Test
    void getQueryWrapper_nullRequest_returnsDefaultFilters() {
        QueryWrapper<QuestionSubmit> w = service.getQueryWrapper(null);
        assertNotNull(w);
        assertTrue(w.getExpression().getNormal().size() > 0);
    }

    @Test
    void getQueryWrapper_emptyCourse_returnsImpossibleSql() {
        when(courseQuestionService.list(any(Wrapper.class))).thenReturn(Collections.emptyList());
        QuestionSubmitQueryRequest r = new QuestionSubmitQueryRequest();
        r.setCourseId(7L);
        QueryWrapper<QuestionSubmit> w = service.getQueryWrapper(r);
        assertNotNull(w);
        assertTrue(w.getExpression().getSqlSegment().contains("1 = 0"));
    }

    @Test
    void getQuestionSubmitVO_otherUserHidesCode() {
        when(userService.isAdminOrTeacher(any())).thenReturn(false);
        QuestionSubmit submit = new QuestionSubmit();
        submit.setUserId(1L);
        submit.setCode("secret");
        submit.setQuestionId(10L);
        submit.setStatus(0);
        submit.setLanguage("java");
        submit.setJudgeInfo("{}");
        User viewer = new User();
        viewer.setId(2L);
        assertEquals(null, service.getQuestionSubmitVO(submit, viewer).getCode());
    }
}
