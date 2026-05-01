package cn.codepractice.oj.controller;

import cn.codepractice.oj.annotation.AuthCheck;
import cn.hutool.json.JSONUtil;
import cn.codepractice.oj.common.BaseResponse;
import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.common.ResultUtils;
import cn.codepractice.oj.constant.UserConstant;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.exception.ThrowUtils;
import cn.codepractice.oj.model.dto.questionsolution.QuestionSolutionAddRequest;
import cn.codepractice.oj.model.dto.questionsolution.QuestionSolutionQueryRequest;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.entity.QuestionSolution;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.model.vo.QuestionVO;
import cn.codepractice.oj.service.QuestionService;
import cn.codepractice.oj.service.QuestionSolutionService;
import cn.codepractice.oj.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping({"/question/solution", "/v1/question/solution"})
@Slf4j
public class QuestionSolutionController {

    @Resource
    private QuestionSolutionService questionSolutionService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;


    /**
     * 
     *
     * @param questionSolutionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRoleAny = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Long> addQuestion(@RequestBody QuestionSolutionAddRequest questionSolutionAddRequest, HttpServletRequest request) {
        if (questionSolutionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionSolution questionSolution = new QuestionSolution();
        BeanUtils.copyProperties(questionSolutionAddRequest, questionSolution);
        User loginUser = userService.getLoginUser(request);
        questionSolution.setUserId(loginUser.getId());
        questionSolution.setCreateTime(new Date());
        // Для старой схемы БД без DEFAULT у is_delete
        questionSolution.setIsDelete(0);
        List<String> tags = questionSolutionAddRequest.getTags();
        if (tags != null) {
            questionSolution.setTags(JSONUtil.toJsonStr(tags));
        }

        boolean result = questionSolutionService.save(questionSolution);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newSolutionId = questionSolution.getSolutionId();
        return ResultUtils.success(newSolutionId);
    }

    /**
     *  id 
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     *  id 
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<?> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 
        if (!question.getUserId().equals(loginUser.getId()) && !userService.isAdminOrTeacher(loginUser)) {
            return ResultUtils.success(questionService.getQuestionVO(question, request));
        }
        return ResultUtils.success(question);
    }

    /**
     * （）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSolution>> listQuestionVOByPage(@RequestBody QuestionSolutionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<QuestionSolution> questionSolutionPage = questionSolutionService.page(new Page<>(current, size),
                questionSolutionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionSolutionService.getSolutionPage(questionSolutionPage, request));
    }

}
