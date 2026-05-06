package cn.codepractice.oj.controller;

import cn.codepractice.oj.annotation.AuthCheck;
import cn.codepractice.oj.common.BaseResponse;
import cn.codepractice.oj.common.DeleteRequest;
import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.common.ResultUtils;
import cn.codepractice.oj.constant.UserConstant;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.exception.ThrowUtils;
import cn.codepractice.oj.model.dto.question.*;
import cn.codepractice.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import cn.codepractice.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import cn.codepractice.oj.model.entity.CourseQuestion;
import cn.codepractice.oj.model.entity.Enrollment;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.entity.QuestionSubmit;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.model.vo.QuestionSubmitVO;
import cn.codepractice.oj.model.vo.QuestionVO;
import cn.codepractice.oj.service.QuestionService;
import cn.codepractice.oj.service.QuestionSpecificationQueryService;
import cn.codepractice.oj.service.QuestionSubmitService;
import cn.codepractice.oj.service.UserService;
import cn.codepractice.oj.service.CourseQuestionService;
import cn.codepractice.oj.service.EnrollmentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** API задач: отправка кода, операции управления, постраничные списки. */
@RestController
@RequestMapping({"/question", "/v1/question"})
@Slf4j
public class QuestionController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private EnrollmentService enrollmentService;

    @Resource
    private CourseQuestionService courseQuestionService;

    @Resource
    private QuestionSpecificationQueryService questionSpecificationQueryService;

    private final static Gson GSON = new Gson();


    @PostMapping("/codeTemplate")
    public BaseResponse<String> getCodeTemplate(@RequestBody CodeTemplateQuery codeTemplateQuery) {
        String templateCode =  questionService.getCodeTemplate(codeTemplateQuery);
        return ResultUtils.success(templateCode);
    }


    /** Отправка решения на проверку (текущий пользователь). */
    @PostMapping("/submit")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final User loginUser = userService.getLoginUser(request);
        long result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(result);
    }


    /** Постраничный список своих отправок по фильтрам. */
    @PostMapping("/submit/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        if (questionSubmitQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        if (current < 1 || size < 1 || size > 500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Некорректная пагинация");
        }
        Page<QuestionSubmit> questionPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionPage, loginUser));
    }

    /** Создание задачи; автор — текущий пользователь. */
    @PostMapping("/add")
    @AuthCheck(mustRoleAny = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        questionService.validQuestion(question, true);
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /** Удаление задачи: автор или администратор. */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        Question oldQuestion = questionService.getById(id);
        if (oldQuestion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // Только автор задачи или администратор
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * Обновление задачи (админ — любая; преподаватель — только своя по userId автора).
     *
     * @param questionUpdateRequest тело запроса
     * @param request               HTTP-запрос (текущий пользователь)
     * @return успех обновления
     */
    @PostMapping("/update")
    @AuthCheck(mustRoleAny = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest,
                                                HttpServletRequest request) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionUpdateRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser) && !Objects.equals(oldQuestion.getUserId(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Можно изменять только свои задачи");
        }
        question.setUserId(oldQuestion.getUserId());
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /** Публичное VO задачи по id. */
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
     * Задача по id: автор и админ/препод видят сущность; остальные — только VO без скрытых полей.
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
        if (!question.getUserId().equals(loginUser.getId()) && !userService.isAdminOrTeacher(loginUser)) {
            return ResultUtils.success(questionService.getQuestionVO(question, request));
        }
        return ResultUtils.success(question);
    }

    /** То же, что GET /get, но с id в пути (ресурсный URL). */
    @GetMapping("/{id}")
    public BaseResponse<?> getQuestionByPath(@PathVariable("id") long id, HttpServletRequest request) {
        return getQuestionById(id, request);
    }

    /** Постраничный список VO; для студента — только задачи курсов с записи. */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        User loginUser = userService.getLoginUserPermitNull(request);
        boolean student = loginUser != null && UserConstant.DEFAULT_ROLE.equals(loginUser.getUserRole());
        long maxPage = student ? 20 : 100;
        ThrowUtils.throwIf(size > maxPage || size < 1, ErrorCode.PARAMS_ERROR);
        // Студент видит на главной только задачи курсов, на которые записан.
        Set<Long> allowedQuestionIds = null;
        if (loginUser != null && UserConstant.DEFAULT_ROLE.equals(loginUser.getUserRole())) {
            QueryWrapper<Enrollment> enrollmentQuery = new QueryWrapper<>();
            enrollmentQuery.eq("user_id", loginUser.getId());
            List<Enrollment> enrollments = enrollmentService.list(enrollmentQuery);
            if (enrollments.isEmpty()) {
                Page<QuestionVO> emptyPage = new Page<>(current, size, 0);
                emptyPage.setRecords(Collections.emptyList());
                return ResultUtils.success(emptyPage);
            }
            List<Long> courseIds = enrollments.stream()
                    .map(Enrollment::getCourseId)
                    .distinct()
                    .collect(Collectors.toList());
            if (courseIds.isEmpty()) {
                Page<QuestionVO> emptyPage = new Page<>(current, size, 0);
                emptyPage.setRecords(Collections.emptyList());
                return ResultUtils.success(emptyPage);
            }
            QueryWrapper<CourseQuestion> courseQuestionQuery = new QueryWrapper<>();
            courseQuestionQuery.in("course_id", courseIds);
            List<CourseQuestion> courseQuestions = courseQuestionService.list(courseQuestionQuery);
            allowedQuestionIds = courseQuestions.stream()
                    .map(CourseQuestion::getQuestionId)
                    .filter(id -> id != null && id > 0)
                    .collect(Collectors.toSet());
            if (allowedQuestionIds.isEmpty()) {
                Page<QuestionVO> emptyPage = new Page<>(current, size, 0);
                emptyPage.setRecords(Collections.emptyList());
                return ResultUtils.success(emptyPage);
            }
        }
        Page<Question> questionPage = questionSpecificationQueryService
                .queryPageBySpecification(questionQueryRequest, allowedQuestionIds);
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /** Постраничный список VO задач текущего пользователя. */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long size = questionQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionSpecificationQueryService
                .queryPageBySpecification(questionQueryRequest, null);
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }


    /** Частичное редактирование задачи автором или админом/преподавателем. */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        long id = questionEditRequest.getId();
        Question oldQuestion = questionService.getById(id);
        if (oldQuestion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdminOrTeacher(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /** Постраничный список сущностей задач (админ или преподаватель). */
    @PostMapping("/list/page")
    @AuthCheck(mustRoleAny = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                   HttpServletRequest request) {
        long size = questionQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 100 || size < 1, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        if (userService.isTeacher(loginUser) && !userService.isAdmin(loginUser)) {
            // В экранах управления преподаватель работает только со своими задачами.
            questionQueryRequest.setUserId(loginUser.getId());
        }
        Page<Question> questionPage = questionSpecificationQueryService
                .queryPageBySpecification(questionQueryRequest, null);
        List<Question> records = questionPage.getRecords();
        questionService.fillSubmitStatsForQuestions(records);
        records = records.stream()
                .peek(question -> {
                    User user = userService.getById(question.getUserId());
                    if (user != null) {
                        question.setUserName(user.getUserName());
                    }
                })
                .collect(Collectors.toList());
        questionPage.setRecords(records);
        return ResultUtils.success(questionPage);
    }

}
