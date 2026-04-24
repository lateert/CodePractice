package cn.codepractice.oj.service;

import cn.codepractice.oj.model.dto.question.QuestionQueryRequest;
import cn.codepractice.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import cn.codepractice.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.entity.QuestionSubmit;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.model.vo.QuestionSubmitVO;
import cn.codepractice.oj.model.vo.QuestionVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;

/**
* @author 86188
* @description 【question_submit()】Service
* @createDate 2023-12-28 10:36:33
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 
     *
     * @param questionId
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionId, User loginUser);


    /**
     * 
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionQueryRequest);

    /**
     * 
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 
     *
     * @param questionPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionPage, User loginUser);

}
