package cn.codepractice.oj.unit;

import cn.codepractice.oj.constant.CommonConstant;
import cn.codepractice.oj.model.dto.questionsolution.QuestionSolutionQueryRequest;
import cn.codepractice.oj.service.impl.QuestionSolutionServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Модульные проверки построения запроса для списка «разборов».
 */
class QuestionSolutionServiceImplUnitTest {

    private final QuestionSolutionServiceImpl service = new QuestionSolutionServiceImpl();

    @Test
    void getQueryWrapper_null_returnsEmptyWrapper() {
        QueryWrapper<?> w = service.getQueryWrapper(null);
        assertNotNull(w);
    }

    @Test
    void getQueryWrapper_withFilters_buildsConditions() {
        QuestionSolutionQueryRequest r = new QuestionSolutionQueryRequest();
        r.setTitle("demo");
        r.setQuestionId(1L);
        r.setSortField("title");
        r.setSortOrder(CommonConstant.SORT_ORDER_ASC);
        QueryWrapper<?> w = service.getQueryWrapper(r);
        assertNotNull(w);
        assertNotNull(w.getSqlSegment());
    }
}
