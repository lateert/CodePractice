package cn.codepractice.oj.service;

import cn.codepractice.oj.model.dto.question.CodeTemplateQuery;
import cn.codepractice.oj.model.dto.question.QuestionQueryRequest;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.vo.QuestionVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author peiYP
* @description 【question()】Service
* @createDate 2023-12-28 10:35:52
*/
public interface QuestionService extends IService<Question> {

    /**
     * 
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * Подставить в сущности фактические submitNum / acceptedNum по таблице отправок (для списка управления).
     */
    void fillSubmitStatsForQuestions(List<Question> questions);

    /**
     * 
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);


    /**
     * 
     * @param codeTemplateQuery
     * @return
     */
    String getCodeTemplate(CodeTemplateQuery codeTemplateQuery);
}
