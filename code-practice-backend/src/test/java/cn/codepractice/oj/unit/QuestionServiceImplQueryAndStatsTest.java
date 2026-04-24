package cn.codepractice.oj.unit;

import cn.codepractice.oj.constant.CommonConstant;
import cn.codepractice.oj.model.dto.question.QuestionQueryRequest;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.entity.QuestionSubmit;
import cn.codepractice.oj.service.QuestionSubmitService;
import cn.codepractice.oj.service.impl.QuestionServiceImpl;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Дополнительные модульные проверки {@link QuestionServiceImpl}: запросы и статистика отправок.
 */
@ExtendWith(MockitoExtension.class)
class QuestionServiceImplQueryAndStatsTest {

    @Mock
    private QuestionSubmitService questionSubmitService;

    private QuestionServiceImpl questionService;

    @BeforeEach
    void setUp() {
        questionService = new QuestionServiceImpl();
        ReflectionTestUtils.setField(questionService, "questionSubmitService", questionSubmitService);
    }

    @Test
    void getQueryWrapper_nullRequest_returnsWrapper() {
        QueryWrapper<Question> w = questionService.getQueryWrapper(null);
        assertNotNull(w);
    }

    @Test
    void getQueryWrapper_withTitleAndCourse() {
        QuestionQueryRequest r = new QuestionQueryRequest();
        r.setTitle("abc");
        r.setCourseId(5L);
        QueryWrapper<Question> w = questionService.getQueryWrapper(r);
        assertNotNull(w);
        assertTrue(w.getSqlSegment().contains("title"));
    }

    @Test
    void getQueryWrapper_onlyWithoutCourse() {
        QuestionQueryRequest r = new QuestionQueryRequest();
        r.setOnlyWithoutCourse(true);
        QueryWrapper<Question> w = questionService.getQueryWrapper(r);
        assertNotNull(w);
        assertTrue(w.getSqlSegment().contains("NOT EXISTS"));
    }

    @Test
    void getQueryWrapper_sortByTitleAsc() {
        QuestionQueryRequest r = new QuestionQueryRequest();
        r.setSortField("title");
        r.setSortOrder(CommonConstant.SORT_ORDER_ASC);
        QueryWrapper<Question> w = questionService.getQueryWrapper(r);
        assertNotNull(w);
    }

    @Test
    void fillSubmitStats_emptyList_noop() {
        questionService.fillSubmitStatsForQuestions(Collections.emptyList());
        questionService.fillSubmitStatsForQuestions(null);
    }

    @Test
    void fillSubmitStats_countsSubmits() {
        Question q = new Question();
        q.setId(10L);
        QuestionSubmit s1 = new QuestionSubmit();
        s1.setQuestionId(10L);
        s1.setIsDelete(0);
        s1.setJudgeInfo("{\"message\":\"Accepted\"}");
        QuestionSubmit s2 = new QuestionSubmit();
        s2.setQuestionId(10L);
        s2.setIsDelete(0);
        s2.setJudgeInfo("{\"message\":\"Wrong Answer\"}");
        when(questionSubmitService.list(any(Wrapper.class))).thenReturn(Arrays.asList(s1, s2));

        questionService.fillSubmitStatsForQuestions(Collections.singletonList(q));
        assertEquals(2, q.getSubmitNum());
        assertEquals(1, q.getAcceptedNum());
    }

}
