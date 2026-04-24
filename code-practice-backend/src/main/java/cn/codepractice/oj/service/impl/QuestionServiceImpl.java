package cn.codepractice.oj.service.impl;
import java.util.List;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.constant.CommonConstant;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.exception.ThrowUtils;
import cn.codepractice.oj.model.dto.question.CodeTemplateQuery;
import cn.codepractice.oj.model.dto.question.QuestionQueryRequest;
import cn.codepractice.oj.model.entity.*;
import cn.codepractice.oj.model.enums.JudgeInfoMessageEnum;
import cn.codepractice.oj.model.enums.QuestionSubmitLanguageEnum;
import cn.codepractice.oj.model.enums.QuestionSubmitStatusEnum;
import cn.codepractice.oj.model.vo.QuestionSubmitVO;
import cn.codepractice.oj.model.vo.QuestionVO;
import cn.codepractice.oj.model.vo.UserVO;
import cn.codepractice.oj.service.QuestionSubmitService;
import cn.codepractice.oj.service.UserService;
import cn.codepractice.oj.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.codepractice.oj.service.QuestionService;
import cn.codepractice.oj.mapper.QuestionMapper;
import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author peiYP
* @description 【question()】Service
* @createDate 2023-12-28 10:35:52
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

    private final static Gson GSON = new Gson();

    @Resource
    private UserService userService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();

        // ，
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Слишком длинный заголовок");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Слишком длинное содержимое");
        }
        if (StringUtils.isNotBlank(answer) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Слишком длинное содержимое");
        }
        if (StringUtils.isNotBlank(judgeCase) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Слишком длинное содержимое");
        }
        if (StringUtils.isNotBlank(judgeConfig) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Слишком длинное содержимое");
        }
    }

    /**
     * 
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {


        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }

        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        Long courseId = questionQueryRequest.getCourseId();
        Boolean onlyWithoutCourse = questionQueryRequest.getOnlyWithoutCourse();
        String tagKeyword = questionQueryRequest.getTagKeyword();

        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        queryWrapper.like(StringUtils.isNotBlank(tagKeyword), "tags", tagKeyword);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq("is_delete", false);

        if (Boolean.TRUE.equals(onlyWithoutCourse)) {
            queryWrapper.apply("NOT EXISTS (SELECT 1 FROM course_question cq WHERE cq.question_id = question.id)");
        } else if (courseId != null && courseId > 0) {
            queryWrapper.apply("EXISTS (SELECT 1 FROM course_question cq WHERE cq.question_id = question.id AND cq.course_id = {0})",
                    courseId);
        }

        applyQuestionOrder(queryWrapper, sortField, sortOrder);
        return queryWrapper;
    }

    private void applyQuestionOrder(QueryWrapper<Question> queryWrapper, String sortField, String sortOrder) {
        boolean asc = CommonConstant.SORT_ORDER_ASC.equals(StringUtils.trimToEmpty(sortOrder));
        String dir = asc ? "ASC" : "DESC";
        if (StringUtils.isBlank(sortField) || !SqlUtils.validSortField(sortField)) {
            queryWrapper.orderByDesc("create_time");
            return;
        }
        switch (sortField) {
            case "title":
                queryWrapper.orderBy(true, asc, "title");
                break;
            case "createTime":
                queryWrapper.orderBy(true, asc, "create_time");
                break;
            case "submitNum":
                queryWrapper.last("ORDER BY (SELECT COUNT(*) FROM question_submit qs WHERE qs.question_id = question.id AND IFNULL(qs.is_delete,0) = 0) "
                        + dir);
                break;
            case "acceptedNum":
                queryWrapper.last("ORDER BY (SELECT COUNT(*) FROM question_submit qs WHERE qs.question_id = question.id AND IFNULL(qs.is_delete,0) = 0 AND qs.judge_info LIKE CONCAT('%', '\\\"message\\\":\\\"Accepted\\\"', '%')) "
                        + dir);
                break;
            default:
                queryWrapper.orderByDesc("create_time");
        }
    }

    @Override
    public void fillSubmitStatsForQuestions(List<Question> questions) {
        if (CollectionUtils.isEmpty(questions)) {
            return;
        }
        List<Long> ids = questions.stream().map(Question::getId).filter(Objects::nonNull).collect(Collectors.toList());
        if (ids.isEmpty()) {
            return;
        }
        List<QuestionSubmit> submits = questionSubmitService.list(Wrappers.lambdaQuery(QuestionSubmit.class)
                .in(QuestionSubmit::getQuestionId, ids));
        Map<Long, List<QuestionSubmit>> byQuestion = submits.stream()
                .filter(s -> s.getIsDelete() == null || s.getIsDelete() == 0)
                .collect(Collectors.groupingBy(QuestionSubmit::getQuestionId));
        for (Question q : questions) {
            List<QuestionSubmit> list = byQuestion.getOrDefault(q.getId(), Collections.emptyList());
            long ac = list.stream().filter(this::isAcceptedSubmit).count();
            q.setSubmitNum(list.size());
            q.setAcceptedNum((int) ac);
        }
    }

    private boolean isAcceptedSubmit(QuestionSubmit questionSubmit) {
        QuestionSubmitVO vo = QuestionSubmitVO.objToVo(questionSubmit);
        return vo.getJudgeInfo() != null
                && JudgeInfoMessageEnum.ACCEPTED.getValue().equals(vo.getJudgeInfo().getMessage());
    }
    

    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 1. 
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUser(userVO);

        // 2. （）
        List<QuestionSubmit> questionSubmits = questionSubmitService.list(
                Wrappers.lambdaQuery(QuestionSubmit.class).eq(QuestionSubmit::getQuestionId, question.getId()));
        long acSubmitCount = questionSubmits.stream().filter(questionSubmit -> {
            QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
            return questionSubmitVO.getJudgeInfo() != null
                    && JudgeInfoMessageEnum.ACCEPTED.getValue().equals(questionSubmitVO.getJudgeInfo().getMessage());
        }).count();
        questionVO.setSubmitNum(questionSubmits.size());
        questionVO.setAcceptedNum((int) acSubmitCount);
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 1. 
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // questionId
        List<Long> questionIds = questionList.stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        // 
        List<QuestionSubmit> questionSubmitList = questionSubmitService.list(Wrappers.lambdaQuery(QuestionSubmit.class)
                .in(QuestionSubmit::getQuestionId, questionIds));

        Map<Long, List<QuestionSubmit>> submitListMap = questionSubmitList.stream()
                .collect(Collectors.groupingBy(QuestionSubmit::getQuestionId));


        // 
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            Long questionId = question.getId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUser(userService.getUserVO(user));
            List<QuestionSubmit> questionSubmits = submitListMap.getOrDefault(questionId, new ArrayList<>());
            long acSubmitCount = questionSubmits.stream().filter(questionSubmit -> {
                QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
                return JudgeInfoMessageEnum.ACCEPTED.getValue().equals(questionSubmitVO.getJudgeInfo().getMessage());
            }).count();
            questionVO.setSubmitNum(questionSubmits.size());
            questionVO.setAcceptedNum((int) acSubmitCount);
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    @Override
    public String getCodeTemplate(CodeTemplateQuery codeTemplateQuery) {
        String language = codeTemplateQuery.getLanguage();
        if (QuestionSubmitLanguageEnum.JAVA.getValue().equals(language)) {
            return ResourceUtil.readUtf8Str("classpath:/codeTemplate/CodeTemplate.java");
        }
        return ResourceUtil.readUtf8Str("classpath:/codeTemplate/CodeTemplate.java");
    }

}




