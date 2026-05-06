package cn.codepractice.oj.service.impl;

import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.constant.CommonConstant;
import cn.codepractice.oj.constant.UserConstant;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.judege.JudgeService;
import cn.codepractice.oj.model.dto.question.QuestionQueryRequest;
import cn.codepractice.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import cn.codepractice.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import cn.codepractice.oj.model.entity.Course;
import cn.codepractice.oj.model.entity.CourseQuestion;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.model.enums.QuestionSubmitLanguageEnum;
import cn.codepractice.oj.model.enums.QuestionSubmitStatusEnum;
import cn.codepractice.oj.model.vo.QuestionSubmitVO;
import cn.codepractice.oj.model.vo.QuestionVO;
import cn.codepractice.oj.model.vo.UserVO;
import cn.codepractice.oj.service.CourseQuestionService;
import cn.codepractice.oj.service.CourseService;
import cn.codepractice.oj.service.EnrollmentService;
import cn.codepractice.oj.service.QuestionService;
import cn.codepractice.oj.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.codepractice.oj.model.entity.QuestionSubmit;
import cn.codepractice.oj.service.QuestionSubmitService;
import cn.codepractice.oj.mapper.QuestionSubmitMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Сервис отправок решений по задачам (таблица question_submit). */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService{

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private CourseQuestionService courseQuestionService;

    @Resource
    private CourseService courseService;

    @Resource
    private EnrollmentService enrollmentService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Язык программирования не поддерживается");
        }

        long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        validateSubmitPermission(question, loginUser);
        long userId = loginUser.getId();
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(userId);

        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Не удалось сохранить отправку");
        }

        Long submitId = questionSubmit.getId();
        CompletableFuture.runAsync(() -> {
            judgeService.doJudge(submitId);
        });
        return submitId;
    }

    private void validateSubmitPermission(Question question, User loginUser) {
        if (userService.isAdmin(loginUser)) {
            return;
        }
        Long loginUserId = loginUser.getId();
        Long questionOwnerId = question.getUserId();
        if (Objects.equals(questionOwnerId, loginUserId)) {
            return;
        }
        List<CourseQuestion> rels = courseQuestionService.list(
                new LambdaQueryWrapper<CourseQuestion>().eq(CourseQuestion::getQuestionId, question.getId()));
        Set<Long> courseIds = rels.stream()
                .map(CourseQuestion::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (courseIds.isEmpty()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Задача недоступна для отправки");
        }
        long enrollmentCount = enrollmentService.count(new LambdaQueryWrapper<cn.codepractice.oj.model.entity.Enrollment>()
                .eq(cn.codepractice.oj.model.entity.Enrollment::getUserId, loginUserId)
                .in(cn.codepractice.oj.model.entity.Enrollment::getCourseId, courseIds));
        if (enrollmentCount <= 0) {
            if (UserConstant.TEACHER_ROLE.equals(loginUser.getUserRole())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Для решения чужих задач нужна запись на курс");
            }
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Сначала запишитесь на курс");
        }
    }


    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        long userId = loginUser.getId();
        if (userId != questionSubmit.getUserId() && !userService.isAdminOrTeacher(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionQueryRequest) {

        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            queryWrapper.eq("is_delete", 0);
            queryWrapper.orderByDesc("create_time");
            return queryWrapper;
        }

        String language = questionQueryRequest.getLanguage();
        Integer status = questionQueryRequest.getStatus();
        Long questionId = questionQueryRequest.getQuestionId();
        Long userId = questionQueryRequest.getUserId();
        Long courseId = questionQueryRequest.getCourseId();
        String questionTitleKeyword = questionQueryRequest.getQuestionTitleKeyword();
        String userNameKeyword = questionQueryRequest.getUserNameKeyword();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        List<Long> restrictQuestionIds = null;

        if (ObjectUtils.isNotEmpty(courseId)) {
            List<CourseQuestion> rels = courseQuestionService.list(
                    new LambdaQueryWrapper<CourseQuestion>().eq(CourseQuestion::getCourseId, courseId));
            Set<Long> qids = rels.stream()
                    .map(CourseQuestion::getQuestionId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (qids.isEmpty()) {
                queryWrapper.apply("1 = 0");
                return queryWrapper;
            }
            restrictQuestionIds = new ArrayList<>(qids);
        }

        if (StringUtils.isNotBlank(questionTitleKeyword)) {
            List<Question> qs = questionService.list(new QueryWrapper<Question>()
                    .like("title", questionTitleKeyword.trim())
                    .eq("is_delete", 0));
            Set<Long> titleQids = qs.stream().map(Question::getId).filter(Objects::nonNull).collect(Collectors.toSet());
            if (titleQids.isEmpty()) {
                queryWrapper.apply("1 = 0");
                return queryWrapper;
            }
            if (restrictQuestionIds != null) {
                restrictQuestionIds.retainAll(titleQids);
                if (restrictQuestionIds.isEmpty()) {
                    queryWrapper.apply("1 = 0");
                    return queryWrapper;
                }
            } else {
                restrictQuestionIds = new ArrayList<>(titleQids);
            }
        }

        if (restrictQuestionIds != null) {
            queryWrapper.in("question_id", restrictQuestionIds);
        }

        if (StringUtils.isNotBlank(userNameKeyword)) {
            List<User> users = userService.list(new QueryWrapper<User>()
                    .like("user_name", userNameKeyword.trim()));
            Set<Long> uids = users.stream().map(User::getId).filter(Objects::nonNull).collect(Collectors.toSet());
            if (uids.isEmpty()) {
                queryWrapper.apply("1 = 0");
                return queryWrapper;
            }
            queryWrapper.in("user_id", uids);
        }

        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "question_id", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("is_delete", 0);

        String sf = StringUtils.isBlank(sortField) ? "createTime" : sortField;
        if (!isAllowedSubmitSortField(sf)) {
            sf = "createTime";
        }
        String orderColumn = mapSubmitSortToColumn(sf);
        boolean isAsc = CommonConstant.SORT_ORDER_ASC.equals(StringUtils.trimToEmpty(sortOrder));
        queryWrapper.orderBy(true, isAsc, orderColumn);

        return queryWrapper;
    }

    private static boolean isAllowedSubmitSortField(String sortField) {
        return "createTime".equals(sortField)
                || "updateTime".equals(sortField)
                || "language".equals(sortField)
                || "status".equals(sortField)
                || "questionId".equals(sortField)
                || "userId".equals(sortField);
    }

    private static String mapSubmitSortToColumn(String sortField) {
        if ("createTime".equals(sortField)) {
            return "create_time";
        }
        if ("updateTime".equals(sortField)) {
            return "update_time";
        }
        if ("questionId".equals(sortField)) {
            return "question_id";
        }
        if ("userId".equals(sortField)) {
            return "user_id";
        }
        return sortField;
    }
    

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        Set<Long> questionIds = questionSubmitList.stream()
                .map(QuestionSubmit::getQuestionId)
                .collect(Collectors.toSet());
        Map<Long, QuestionVO> questionVOMap = questionService.listByIds(questionIds).stream()
                .map(QuestionVO::objToVo)
                .collect(Collectors.toMap(QuestionVO::getId, Function.identity()));


        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> {
                    QuestionSubmitVO questionSubmitVO = getQuestionSubmitVO(questionSubmit, loginUser);
                    User submitUser = userService.getById(questionSubmit.getUserId());
                    questionSubmitVO.setUserName(submitUser != null ? submitUser.getUserName() : "—");
                    QuestionSubmitStatusEnum st = QuestionSubmitStatusEnum.getEnumByValue(questionSubmitVO.getStatus());
                    questionSubmitVO.setStatusStr(st != null ? st.getText() : "—");
                    questionSubmitVO.setQuestionVO(questionVOMap.get(questionSubmitVO.getQuestionId()));
                    return questionSubmitVO;
                })
                .collect(Collectors.toList());
        fillCourseTitlesForSubmits(questionSubmitVOList);
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    private void fillCourseTitlesForSubmits(List<QuestionSubmitVO> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return;
        }
        Set<Long> qids = vos.stream()
                .map(QuestionSubmitVO::getQuestionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (qids.isEmpty()) {
            vos.forEach(v -> v.setCourseTitles(Collections.emptyList()));
            return;
        }
        List<CourseQuestion> rels = courseQuestionService.list(
                new LambdaQueryWrapper<CourseQuestion>().in(CourseQuestion::getQuestionId, qids));
        if (CollectionUtils.isEmpty(rels)) {
            vos.forEach(v -> v.setCourseTitles(Collections.emptyList()));
            return;
        }
        Set<Long> courseIds = rels.stream()
                .map(CourseQuestion::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Course> courseMap = courseIds.isEmpty()
                ? Collections.emptyMap()
                : courseService.listByIds(courseIds).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Course::getId, c -> c, (a, b) -> a));
        Map<Long, LinkedHashSet<String>> qToTitles = new HashMap<>();
        for (CourseQuestion r : rels) {
            if (r.getQuestionId() == null) {
                continue;
            }
            Course c = courseMap.get(r.getCourseId());
            if (c != null && StringUtils.isNotBlank(c.getTitle())) {
                qToTitles.computeIfAbsent(r.getQuestionId(), k -> new LinkedHashSet<>()).add(c.getTitle());
            }
        }
        for (QuestionSubmitVO v : vos) {
            LinkedHashSet<String> titles = qToTitles.get(v.getQuestionId());
            v.setCourseTitles(titles == null
                    ? Collections.emptyList()
                    : new ArrayList<>(titles));
        }
    }

}




