package cn.codepractice.oj.controller;

import cn.codepractice.oj.annotation.AuthCheck;
import cn.codepractice.oj.common.BaseResponse;
import cn.codepractice.oj.common.DeleteRequest;
import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.common.ResultUtils;
import cn.codepractice.oj.constant.CommonConstant;
import cn.codepractice.oj.constant.UserConstant;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.model.dto.course.CourseAddRequest;
import cn.codepractice.oj.model.dto.course.CourseEnrollRequest;
import cn.codepractice.oj.model.dto.course.CoursePublishRequest;
import cn.codepractice.oj.model.dto.course.CourseQuestionBindRequest;
import cn.codepractice.oj.model.dto.course.CourseUpdateRequest;
import cn.codepractice.oj.model.entity.Course;
import cn.codepractice.oj.model.entity.CourseQuestion;
import cn.codepractice.oj.model.entity.Enrollment;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.model.vo.QuestionVO;
import cn.codepractice.oj.service.CourseQuestionService;
import cn.codepractice.oj.service.CourseService;
import cn.codepractice.oj.service.EnrollmentService;
import cn.codepractice.oj.service.QuestionService;
import cn.codepractice.oj.service.UserService;
import cn.codepractice.oj.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/course", "/v1/course"})
public class CourseController {

    @Resource
    private CourseService courseService;

    @Resource
    private UserService userService;

    @Resource
    private CourseQuestionService courseQuestionService;

    @Resource
    private QuestionService questionService;

    @Resource
    private EnrollmentService enrollmentService;

    /**
     * Создать курс (только админ)
     */
    @PostMapping("/add")
    public BaseResponse<Long> addCourse(@RequestBody CourseAddRequest addRequest,
                                        HttpServletRequest request) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdminOrTeacher(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Нет прав для создания курса");
        }

        Course course = new Course();
        course.setTitle(addRequest.getTitle());
        course.setDescription(addRequest.getDescription());
        course.setAuthorId(loginUser.getId());
        course.setIsPublished(Boolean.TRUE.equals(addRequest.getPublished()) ? 1 : 0);

        boolean save = courseService.save(course);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Не удалось создать курс");
        }
        return ResultUtils.success(course.getId());
    }

    /**
     * Список курсов (пагинация). Необязательно: title — подстрока в названии; isPublished — 0 или 1.
     * Черновики чужих курсов не показываются: только админ видит все; преподаватель — опубликованные и свои;
     * студент и аноним — только опубликованные.
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Course>> listCourseByPage(@RequestParam long current,
                                                       @RequestParam long pageSize,
                                                       @RequestParam(required = false) String title,
                                                       @RequestParam(required = false) Integer isPublished,
                                                       HttpServletRequest request) {
        if (current <= 0 || pageSize <= 0 || pageSize > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUserPermitNull(request);
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        if (title != null && !title.isBlank()) {
            wrapper.like("title", title.trim());
        }
        if (userService.isAdmin(loginUser)) {
            if (isPublished != null && (isPublished == 0 || isPublished == 1)) {
                wrapper.eq("is_published", isPublished);
            }
        } else if (userService.isTeacher(loginUser)) {
            if (isPublished != null && isPublished == 1) {
                wrapper.eq("is_published", 1);
            } else if (isPublished != null && isPublished == 0) {
                wrapper.eq("is_published", 0).eq("author_id", loginUser.getId());
            } else {
                wrapper.and(w -> w.eq("is_published", 1)
                        .or(w2 -> w2.eq("author_id", loginUser.getId())));
            }
        } else {
            wrapper.eq("is_published", 1);
        }
        wrapper.orderByDesc("update_time");
        Page<Course> page = courseService.page(new Page<>(current, pageSize), wrapper);
        return ResultUtils.success(page);
    }

    /**
     * Публичный список опубликованных курсов для студентов.
     * keyword — подстрока в названии или описании; sortField — title | createTime | updateTime.
     */
    @GetMapping("/list/public")
    public BaseResponse<Page<Course>> listPublicCourseByPage(@RequestParam long current,
                                                             @RequestParam long pageSize,
                                                             @RequestParam(required = false) String keyword,
                                                             @RequestParam(required = false) String sortField,
                                                             @RequestParam(required = false) String sortOrder) {
        if (current <= 0 || pageSize <= 0 || pageSize > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("is_published", 1);
        if (StringUtils.isNotBlank(keyword)) {
            String k = keyword.trim();
            wrapper.and(w -> w.like("title", k).or().like("description", k));
        }
        applyPublicCourseOrder(wrapper, sortField, sortOrder);
        Page<Course> page = courseService.page(new Page<>(current, pageSize), wrapper);
        return ResultUtils.success(page);
    }

    private static void applyPublicCourseOrder(QueryWrapper<Course> wrapper, String sortField, String sortOrder) {
        boolean asc = CommonConstant.SORT_ORDER_ASC.equals(StringUtils.trimToEmpty(sortOrder));
        String col = null;
        if (SqlUtils.validSortField(sortField)) {
            switch (sortField) {
                case "title":
                    col = "title";
                    break;
                case "createTime":
                    col = "create_time";
                    break;
                case "updateTime":
                    col = "update_time";
                    break;
                default:
                    break;
            }
        }
        if (col != null) {
            wrapper.orderBy(true, asc, col);
        } else {
            wrapper.orderByDesc("update_time");
        }
    }

    /**
     * Редактировать курс (админ или преподаватель)
     */
    @PostMapping("/update")
    @AuthCheck(mustRoleAny = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Boolean> updateCourse(@RequestBody CourseUpdateRequest updateRequest,
                                              HttpServletRequest request) {
        if (updateRequest == null || updateRequest.getId() == null || updateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Course course = courseService.getById(updateRequest.getId());
        if (course == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Курс не найден");
        }
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser) && !Objects.equals(course.getAuthorId(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Можно менять только свои курсы");
        }
        if (updateRequest.getTitle() != null) {
            course.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getDescription() != null) {
            course.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getPublished() != null) {
            course.setIsPublished(Boolean.TRUE.equals(updateRequest.getPublished()) ? 1 : 0);
        }
        boolean ok = courseService.updateById(course);
        return ResultUtils.success(ok);
    }

    /**
     * Опубликовать / закрыть курс (teacher/admin)
     */
    @PostMapping("/publish")
    @AuthCheck(mustRoleAny = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Boolean> publishCourse(@RequestBody CoursePublishRequest publishRequest,
                                               HttpServletRequest request) {
        if (publishRequest == null || publishRequest.getCourseId() == null || publishRequest.getCourseId() <= 0
                || publishRequest.getPublished() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Course course = courseService.getById(publishRequest.getCourseId());
        if (course == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Курс не найден");
        }
        // Админ может менять любой курс, преподаватель — только свой
        if (!userService.isAdmin(loginUser) && !Objects.equals(course.getAuthorId(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Можно менять только свои курсы");
        }
        course.setIsPublished(Boolean.TRUE.equals(publishRequest.getPublished()) ? 1 : 0);
        boolean ok = courseService.updateById(course);
        return ResultUtils.success(ok);
    }

    /**
     * Удалить курс (админ или автор-преподаватель)
     */
    @PostMapping("/delete")
    @AuthCheck(mustRoleAny = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Boolean> deleteCourse(@RequestBody DeleteRequest deleteRequest,
                                               HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Course course = courseService.getById(deleteRequest.getId());
        if (course == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Курс не найден");
        }
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser) && !Objects.equals(course.getAuthorId(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Можно удалять только свои курсы");
        }
        boolean ok = courseService.removeById(deleteRequest.getId());
        return ResultUtils.success(ok);
    }

    /**
     * Привязать задачу к курсу (админ — любой курс; преподаватель — только свой курс).
     */
    @PostMapping("/question/add")
    public BaseResponse<Boolean> addQuestionToCourse(@RequestBody CourseQuestionBindRequest bindRequest,
                                                     HttpServletRequest request) {
        if (bindRequest == null
                || bindRequest.getCourseId() == null || bindRequest.getCourseId() <= 0
                || bindRequest.getQuestionId() == null || bindRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdminOrTeacher(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Нет прав для привязки задачи к курсу");
        }

        Course course = courseService.getById(bindRequest.getCourseId());
        if (course == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Курс не найден");
        }
        if (!userService.isAdmin(loginUser) && !Objects.equals(course.getAuthorId(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Можно редактировать состав только своих курсов");
        }
        Question question = questionService.getById(bindRequest.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Задача не найдена");
        }

        // если позиция не указана — ставим в конец
        Integer position = bindRequest.getPosition();
        if (position == null) {
            LambdaQueryWrapper<CourseQuestion> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CourseQuestion::getCourseId, bindRequest.getCourseId());
            List<CourseQuestion> existing = courseQuestionService.list(wrapper);
            int maxPosition = existing.stream()
                    .filter(cq -> cq.getPosition() != null)
                    .map(CourseQuestion::getPosition)
                    .max(Integer::compareTo)
                    .orElse(0);
            position = maxPosition + 1;
        }

        CourseQuestion relation = new CourseQuestion();
        relation.setCourseId(bindRequest.getCourseId());
        relation.setQuestionId(bindRequest.getQuestionId());
        relation.setPosition(position);

        boolean save = courseQuestionService.save(relation);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Не удалось привязать задачу к курсу");
        }
        return ResultUtils.success(true);
    }

    /**
     * Список задач курса в порядке position
     */
    @GetMapping("/question/list")
    public BaseResponse<List<QuestionVO>> listCourseQuestions(@RequestParam Long courseId,
                                                              HttpServletRequest request) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Курс не найден");
        }

        LambdaQueryWrapper<CourseQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseQuestion::getCourseId, courseId);
        List<CourseQuestion> relations = courseQuestionService.list(wrapper);
        if (relations.isEmpty()) {
            return ResultUtils.success(new ArrayList<>());
        }

        List<Long> questionIds = relations.stream()
                .map(CourseQuestion::getQuestionId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (questionIds.isEmpty()) {
            return ResultUtils.success(new ArrayList<>());
        }

        List<Question> questions = questionService.listByIds(questionIds);
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        List<QuestionVO> result = new ArrayList<>();
        relations.stream()
                .sorted(Comparator.comparing(cq -> cq.getPosition() == null ? 0 : cq.getPosition()))
                .forEach(relation -> {
                    Question q = questionMap.get(relation.getQuestionId());
                    if (q != null) {
                        QuestionVO vo = questionService.getQuestionVO(q, request);
                        result.add(vo);
                    }
                });

        return ResultUtils.success(result);
    }

    /**
     * Для списка задач вернуть, в каких курсах каждая задача используется (для отображения в «Управление задачами»).
     * Параметр questionIds — через запятую, например 1,2,3.
     */
    @GetMapping("/question/courses-by-questions")
    @AuthCheck(mustRoleAny = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Map<String, List<Course>>> getCoursesByQuestionIds(@RequestParam String questionIds) {
        if (questionIds == null || questionIds.isBlank()) {
            return ResultUtils.success(new java.util.HashMap<>());
        }
        List<Long> ids = new ArrayList<>();
        for (String s : questionIds.split(",")) {
            s = s.trim();
            if (!s.isEmpty()) {
                try {
                    ids.add(Long.parseLong(s));
                } catch (NumberFormatException ignored) {
                }
                if (ids.size() >= 500) break;
            }
        }
        if (ids.isEmpty()) {
            return ResultUtils.success(new java.util.HashMap<>());
        }
        LambdaQueryWrapper<CourseQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CourseQuestion::getQuestionId, ids);
        List<CourseQuestion> relations = courseQuestionService.list(wrapper);
        List<Long> courseIds = relations.stream()
                .map(CourseQuestion::getCourseId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        // Строковые ключи — совпадают с id задачи в JSON без потери точности в JavaScript
        Map<String, List<Course>> questionIdToCourses = new java.util.LinkedHashMap<>();
        for (Long qid : ids) {
            questionIdToCourses.put(String.valueOf(qid), new ArrayList<>());
        }
        if (courseIds.isEmpty()) {
            return ResultUtils.success(questionIdToCourses);
        }
        List<Course> courses = courseService.listByIds(courseIds);
        Map<Long, Course> courseMap = courses.stream().collect(Collectors.toMap(Course::getId, c -> c));
        for (CourseQuestion r : relations) {
            Course c = courseMap.get(r.getCourseId());
            if (c != null && r.getQuestionId() != null) {
                questionIdToCourses.computeIfAbsent(String.valueOf(r.getQuestionId()), k -> new ArrayList<>()).add(c);
            }
        }
        return ResultUtils.success(questionIdToCourses);
    }

    /**
     * Записаться на курс (студент)
     */
    @PostMapping("/enroll")
    public BaseResponse<Boolean> enrollCourse(@RequestBody CourseEnrollRequest enrollRequest,
                                              HttpServletRequest request) {
        if (enrollRequest == null || enrollRequest.getCourseId() == null || enrollRequest.getCourseId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long courseId = enrollRequest.getCourseId();
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Курс не найден");
        }
        if (course.getIsPublished() == null || course.getIsPublished() != 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Запись на курс возможна только после публикации");
        }

        QueryWrapper<Enrollment> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId)
                .eq("user_id", loginUser.getId());
        Enrollment exists = enrollmentService.getOne(wrapper);
        if (exists != null) {
            // уже записан — считаем операцию успешной
            return ResultUtils.success(true);
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setCourseId(courseId);
        enrollment.setUserId(loginUser.getId());
        enrollment.setEnrolledAt(new java.util.Date());
        boolean save = enrollmentService.save(enrollment);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Не удалось записаться на курс");
        }
        return ResultUtils.success(true);
    }

    /**
     * Отписаться от курса (студент)
     */
    @PostMapping("/enroll/cancel")
    public BaseResponse<Boolean> cancelEnrollment(@RequestBody CourseEnrollRequest enrollRequest,
                                                  HttpServletRequest request) {
        if (enrollRequest == null || enrollRequest.getCourseId() == null || enrollRequest.getCourseId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long courseId = enrollRequest.getCourseId();

        QueryWrapper<Enrollment> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId)
                .eq("user_id", loginUser.getId());
        boolean removed = enrollmentService.remove(wrapper);
        if (!removed) {
            // если записи не было — всё равно не считаем это ошибкой
            return ResultUtils.success(true);
        }
        return ResultUtils.success(true);
    }

    /**
     * Список курсов, на которые записан текущий пользователь
     */
    @GetMapping("/enroll/my")
    public BaseResponse<List<Course>> listMyCourses(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<Enrollment> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", loginUser.getId());
        List<Enrollment> enrollments = enrollmentService.list(wrapper);
        if (enrollments.isEmpty()) {
            return ResultUtils.success(new ArrayList<>());
        }
        List<Long> courseIds = enrollments.stream()
                .map(Enrollment::getCourseId)
                .distinct()
                .collect(Collectors.toList());
        if (courseIds.isEmpty()) {
            return ResultUtils.success(new ArrayList<>());
        }
        List<Course> courses = courseService.listByIds(courseIds);
        return ResultUtils.success(courses);
    }
}

