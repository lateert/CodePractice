package cn.codepractice.oj.controller;

import cn.codepractice.oj.annotation.AuthCheck;
import cn.codepractice.oj.common.BaseResponse;
import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.common.ResultUtils;
import cn.codepractice.oj.constant.UserConstant;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.model.entity.Course;
import cn.codepractice.oj.model.entity.CourseQuestion;
import cn.codepractice.oj.model.entity.Enrollment;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.entity.QuestionSubmit;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.model.enums.JudgeInfoMessageEnum;
import cn.codepractice.oj.model.vo.CourseSummaryVO;
import cn.codepractice.oj.model.vo.CourseProgressVO;
import cn.codepractice.oj.model.vo.QuestionProgressItemVO;
import cn.codepractice.oj.model.vo.QuestionSubmitVO;
import cn.codepractice.oj.model.vo.QuestionSummaryVO;
import cn.codepractice.oj.model.vo.StudentProgressVO;
import cn.codepractice.oj.service.CourseQuestionService;
import cn.codepractice.oj.service.CourseService;
import cn.codepractice.oj.service.EnrollmentService;
import cn.codepractice.oj.service.QuestionService;
import cn.codepractice.oj.service.QuestionSubmitService;
import cn.codepractice.oj.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Системная статистика для администратора (курсы, задачи, отправки).
 */
@RestController
@RequestMapping({"/statistics", "/v1/statistics"})
public class StatisticsController {

    @Resource
    private CourseService courseService;

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private EnrollmentService enrollmentService;

    @Resource
    private CourseQuestionService courseQuestionService;

    @Resource
    private UserService userService;

    /**
     * Сводка: количество курсов, задач, отправок, успешных отправок.
     */
    @GetMapping("/summary")
    @AuthCheck(mustRoleAny = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Map<String, Long>> getSummary(HttpServletRequest request) {
        long courseCount = courseService.count();
        long questionCount = questionService.count();
        QueryWrapper<cn.codepractice.oj.model.entity.QuestionSubmit> qAll = new QueryWrapper<>();
        qAll.eq("is_delete", 0);
        List<QuestionSubmit> allSubmissions = questionSubmitService.list(qAll);
        Map<Long, String> userRoleMap = buildUserRoleMap(allSubmissions.stream()
                .map(QuestionSubmit::getUserId)
                .collect(Collectors.toSet()));
        long submissionCount = allSubmissions.stream()
                .filter(s -> !UserConstant.ADMIN_ROLE.equals(userRoleMap.get(s.getUserId())))
                .count();
        long acceptedCount = allSubmissions.stream()
                .filter(s -> !UserConstant.ADMIN_ROLE.equals(userRoleMap.get(s.getUserId())))
                .filter(StatisticsController::isVerdictAccepted)
                .count();

        Map<String, Long> map = new HashMap<>();
        map.put("courseCount", courseCount);
        map.put("questionCount", questionCount);
        map.put("submissionCount", submissionCount);
        map.put("acceptedCount", acceptedCount);
        return ResultUtils.success(map);
    }

    /**
     * Отчёт по успеваемости по курсу: записанные студенты и по каждой задаче курса — число отправок и успешных.
     */
    @GetMapping("/progress/course/{courseId}")
    @AuthCheck(mustRoleAny = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<CourseProgressVO> getCourseProgress(@PathVariable Long courseId, HttpServletRequest request) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Курс не найден");
        }

        QueryWrapper<Enrollment> enrollWrapper = new QueryWrapper<>();
        enrollWrapper.eq("course_id", courseId);
        List<Enrollment> enrollments = enrollmentService.list(enrollWrapper);
        List<Long> userIds = enrollments.stream()
                .map(Enrollment::getUserId)
                .distinct()
                .collect(Collectors.toList());

        QueryWrapper<CourseQuestion> cqWrapper = new QueryWrapper<>();
        cqWrapper.eq("course_id", courseId).orderByAsc("position");
        List<CourseQuestion> courseQuestions = courseQuestionService.list(cqWrapper);
        List<Long> questionIds = courseQuestions.stream()
                .map(CourseQuestion::getQuestionId)
                .collect(Collectors.toList());

        Map<Long, String> questionTitles = new HashMap<>();
        if (!questionIds.isEmpty()) {
            List<Question> questions = questionService.listByIds(questionIds);
            for (Question q : questions) {
                questionTitles.put(q.getId(), q.getTitle());
            }
        }
        Map<Long, String> userAccounts = new HashMap<>();
        Map<Long, String> userNames = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userService.listByIds(userIds);
            for (User u : users) {
                userAccounts.put(u.getId(), u.getUserAccount());
                userNames.put(u.getId(), normalizeProgressDisplayName(u.getUserName()));
            }
        }

        Map<String, Long> submitCounts = new HashMap<>();
        Map<String, Long> acceptedCounts = new HashMap<>();
        Map<Long, Long> questionSubmitTotals = new HashMap<>();
        Map<Long, Long> questionAcceptedTotals = new HashMap<>();
        Map<Long, Set<Long>> questionAttemptedUsers = new HashMap<>();
        Map<Long, Set<Long>> questionSolvedUsers = new HashMap<>();
        if (!userIds.isEmpty() && !questionIds.isEmpty()) {
            Set<Long> userIdSet = new HashSet<>(userIds);
            Set<Long> questionIdSet = new HashSet<>(questionIds);
            QueryWrapper<QuestionSubmit> qsWrapper = new QueryWrapper<>();
            qsWrapper.eq("is_delete", 0)
                    .in("user_id", userIds)
                    .in("question_id", questionIds);
            List<QuestionSubmit> selectedSubmissions = questionSubmitService.list(qsWrapper);
            Map<Long, String> userRoleMap = buildUserRoleMap(selectedSubmissions.stream()
                    .map(QuestionSubmit::getUserId)
                    .collect(Collectors.toSet()));
            for (QuestionSubmit s : selectedSubmissions) {
                String submitterRole = userRoleMap.get(s.getUserId());
                if (UserConstant.ADMIN_ROLE.equals(submitterRole)) {
                    continue;
                }
                if (!userIdSet.contains(s.getUserId()) || !questionIdSet.contains(s.getQuestionId())) {
                    continue;
                }
                String key = s.getUserId() + ":" + s.getQuestionId();
                submitCounts.put(key, submitCounts.getOrDefault(key, 0L) + 1L);
                questionSubmitTotals.put(
                        s.getQuestionId(),
                        questionSubmitTotals.getOrDefault(s.getQuestionId(), 0L) + 1L
                );
                questionAttemptedUsers.computeIfAbsent(s.getQuestionId(), k -> new HashSet<>()).add(s.getUserId());
                if (isVerdictAccepted(s)) {
                    acceptedCounts.put(key, acceptedCounts.getOrDefault(key, 0L) + 1L);
                    questionAcceptedTotals.put(
                            s.getQuestionId(),
                            questionAcceptedTotals.getOrDefault(s.getQuestionId(), 0L) + 1L
                    );
                    questionSolvedUsers.computeIfAbsent(s.getQuestionId(), k -> new HashSet<>()).add(s.getUserId());
                }
            }
        }

        List<QuestionProgressItemVO> courseQuestionsList = new ArrayList<>();
        for (Long qid : questionIds) {
            QuestionProgressItemVO item = new QuestionProgressItemVO();
            item.setQuestionId(qid);
            item.setQuestionTitle(questionTitles.getOrDefault(qid, ""));
            item.setSubmitCount(0L);
            item.setAcceptedCount(0L);
            courseQuestionsList.add(item);
        }

        List<StudentProgressVO> students = new ArrayList<>();
        for (Long uid : userIds) {
            StudentProgressVO sp = new StudentProgressVO();
            sp.setUserId(uid);
            sp.setUserAccount(userAccounts.getOrDefault(uid, ""));
            sp.setUserName(userNames.getOrDefault(uid, ""));
            List<QuestionProgressItemVO> items = new ArrayList<>();
            long studentSubmitTotal = 0L;
            long studentAcceptedTotal = 0L;
            for (Long qid : questionIds) {
                QuestionProgressItemVO item = new QuestionProgressItemVO();
                item.setQuestionId(qid);
                item.setQuestionTitle(questionTitles.getOrDefault(qid, ""));
                String key = uid + ":" + qid;
                long submitCount = submitCounts.getOrDefault(key, 0L);
                long acceptedCount = acceptedCounts.getOrDefault(key, 0L);
                item.setSubmitCount(submitCount);
                item.setAcceptedCount(acceptedCount);
                studentSubmitTotal += submitCount;
                studentAcceptedTotal += acceptedCount;
                items.add(item);
            }
            sp.setSubmitTotal(studentSubmitTotal);
            sp.setAcceptedTotal(studentAcceptedTotal);
            sp.setSuccessRate(toPercent(studentAcceptedTotal, studentSubmitTotal));
            sp.setQuestionProgress(items);
            students.add(sp);
        }

        List<QuestionSummaryVO> questionSummaries = new ArrayList<>();
        long courseSubmitTotal = 0L;
        long courseAcceptedTotal = 0L;
        for (Long qid : questionIds) {
            long submitTotal = questionSubmitTotals.getOrDefault(qid, 0L);
            long acceptedTotal = questionAcceptedTotals.getOrDefault(qid, 0L);
            courseSubmitTotal += submitTotal;
            courseAcceptedTotal += acceptedTotal;

            QuestionSummaryVO summary = new QuestionSummaryVO();
            summary.setQuestionId(qid);
            summary.setQuestionTitle(questionTitles.getOrDefault(qid, ""));
            summary.setSubmitTotal(submitTotal);
            summary.setAcceptedTotal(acceptedTotal);
            summary.setSuccessRate(toPercent(acceptedTotal, submitTotal));
            summary.setAttemptedStudents((long) questionAttemptedUsers.getOrDefault(qid, new HashSet<>()).size());
            summary.setSolvedStudents((long) questionSolvedUsers.getOrDefault(qid, new HashSet<>()).size());
            questionSummaries.add(summary);
        }

        CourseSummaryVO courseSummary = new CourseSummaryVO();
        courseSummary.setStudentCount((long) userIds.size());
        courseSummary.setQuestionCount((long) questionIds.size());
        courseSummary.setSubmitTotal(courseSubmitTotal);
        courseSummary.setAcceptedTotal(courseAcceptedTotal);
        courseSummary.setSuccessRate(toPercent(courseAcceptedTotal, courseSubmitTotal));

        CourseProgressVO vo = new CourseProgressVO();
        vo.setCourseId(courseId);
        vo.setCourseTitle(course.getTitle());
        vo.setCourseSummary(courseSummary);
        vo.setCourseQuestions(courseQuestionsList);
        vo.setQuestionSummaries(questionSummaries);
        vo.setStudents(students);
        return ResultUtils.success(vo);
    }

    private double toPercent(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0D;
        }
        return Math.round((numerator * 10000.0) / denominator) / 100.0;
    }

    private static String normalizeProgressDisplayName(String raw) {
        if (raw == null) {
            return "";
        }
        String t = raw.trim();
        if (t.isEmpty() || "null".equalsIgnoreCase(t)) {
            return "";
        }
        return t;
    }

    private static boolean isVerdictAccepted(QuestionSubmit s) {
        QuestionSubmitVO vo = QuestionSubmitVO.objToVo(s);
        return vo.getJudgeInfo() != null
                && JudgeInfoMessageEnum.ACCEPTED.getValue().equals(vo.getJudgeInfo().getMessage());
    }

    private Map<Long, String> buildUserRoleMap(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }
        List<User> users = userService.listByIds(userIds);
        Map<Long, String> result = new HashMap<>();
        for (User user : users) {
            if (user != null && user.getId() != null) {
                result.put(user.getId(), user.getUserRole());
            }
        }
        return result;
    }
}
