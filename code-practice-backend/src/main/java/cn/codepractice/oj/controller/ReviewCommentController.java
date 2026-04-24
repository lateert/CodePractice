package cn.codepractice.oj.controller;

import cn.codepractice.oj.common.BaseResponse;
import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.common.ResultUtils;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.model.dto.review.ReviewCommentAddRequest;
import cn.codepractice.oj.model.dto.review.ReviewCommentUpdateStatusRequest;
import cn.codepractice.oj.model.entity.QuestionSubmit;
import cn.codepractice.oj.model.entity.ReviewComment;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.service.QuestionSubmitService;
import cn.codepractice.oj.service.ReviewCommentService;
import cn.codepractice.oj.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Эндпоинты для комментариев code review
 */
@RestController
@RequestMapping({"/review/comment", "/v1/review/comment"})
public class ReviewCommentController {

    @Resource
    private ReviewCommentService reviewCommentService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * Создать комментарий к отправке
     */
    @PostMapping("/add")
    public BaseResponse<Long> addReviewComment(@RequestBody ReviewCommentAddRequest addRequest,
                                               HttpServletRequest request) {
        if (addRequest == null
                || addRequest.getSubmissionId() == null || addRequest.getSubmissionId() <= 0
                || addRequest.getCommentText() == null || addRequest.getCommentText().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);

        QuestionSubmit submission = questionSubmitService.getById(addRequest.getSubmissionId());
        if (submission == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Отправка не найдена");
        }

        ReviewComment comment = new ReviewComment();
        comment.setSubmissionId(addRequest.getSubmissionId());
        comment.setReviewerId(loginUser.getId());
        comment.setLineNumber(addRequest.getLineNumber());
        comment.setCommentText(addRequest.getCommentText().trim());
        comment.setStatus("OPEN");

        boolean saved = reviewCommentService.save(comment);
        if (!saved) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Не удалось сохранить комментарий");
        }
        return ResultUtils.success(comment.getId());
    }

    /**
     * Список комментариев по submissionId
     */
    @GetMapping("/list")
    public BaseResponse<List<ReviewComment>> listBySubmission(@RequestParam Long submissionId,
                                                              HttpServletRequest request) {
        if (submissionId == null || submissionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QuestionSubmit submission = questionSubmitService.getById(submissionId);
        if (submission == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Отправка не найдена");
        }
        if (!submission.getUserId().equals(loginUser.getId()) && !userService.isAdminOrTeacher(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        LambdaQueryWrapper<ReviewComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReviewComment::getSubmissionId, submissionId)
                .orderByAsc(ReviewComment::getCreateTime);
        List<ReviewComment> list = reviewCommentService.list(wrapper);
        return ResultUtils.success(list);
    }

    /**
     * Обновить статус комментария (OPEN / RESOLVED)
     */
    @PostMapping("/status")
    public BaseResponse<Boolean> updateStatus(@RequestBody ReviewCommentUpdateStatusRequest statusRequest,
                                              HttpServletRequest request) {
        if (statusRequest == null
                || statusRequest.getId() == null || statusRequest.getId() <= 0
                || statusRequest.getStatus() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String status = statusRequest.getStatus().trim().toUpperCase();
        if (!"OPEN".equals(status) && !"RESOLVED".equals(status)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Недопустимый статус");
        }

        User loginUser = userService.getLoginUser(request);

        ReviewComment comment = reviewCommentService.getById(statusRequest.getId());
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Комментарий не найден");
        }
        // разрешаем менять статус только автору комментария или админу
        if (!comment.getReviewerId().equals(loginUser.getId()) && !userService.isAdminOrTeacher(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        comment.setStatus(status);
        boolean updated = reviewCommentService.updateById(comment);
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Не удалось обновить статус");
        }
        return ResultUtils.success(true);
    }
}

