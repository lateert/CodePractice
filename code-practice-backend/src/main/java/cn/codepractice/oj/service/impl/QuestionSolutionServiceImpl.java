package cn.codepractice.oj.service.impl;

import cn.codepractice.oj.constant.CommonConstant;
import cn.codepractice.oj.mapper.QuestionSolutionMapper;
import cn.codepractice.oj.model.dto.question.QuestionQueryRequest;
import cn.codepractice.oj.model.dto.questionsolution.QuestionSolutionQueryRequest;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.entity.QuestionSolution;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.model.vo.QuestionVO;
import cn.codepractice.oj.model.vo.UserVO;
import cn.codepractice.oj.service.QuestionService;
import cn.codepractice.oj.service.QuestionSolutionService;
import cn.codepractice.oj.service.UserService;
import cn.codepractice.oj.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author 86188
* @description 【question_solution()】Service
* @createDate 2024-01-08 15:59:12
*/
@Service
public class QuestionSolutionServiceImpl extends ServiceImpl<QuestionSolutionMapper, QuestionSolution>
    implements QuestionSolutionService {


    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;


    /**
     * 
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSolution> getQueryWrapper(QuestionSolutionQueryRequest questionQueryRequest) {

        QueryWrapper<QuestionSolution> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }

        Long solutionId = questionQueryRequest.getSolutionId();
        String title = questionQueryRequest.getTitle();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        Long questionId = questionQueryRequest.getQuestionId();


        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.eq(ObjectUtils.isNotEmpty(solutionId), "solution_id", solutionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "question_id", questionId);
        queryWrapper.eq("is_delete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<QuestionSolution> getSolutionPage(Page<QuestionSolution> questionSolutionPage, HttpServletRequest request) {

        // 
        List<QuestionSolution> questionSolutionList = questionSolutionPage.getRecords();

        if (questionSolutionList.isEmpty()) {
            return questionSolutionPage;
        }

        // map
        Set<Long> userIdSet = questionSolutionList.stream()
                .map(QuestionSolution::getUserId)
                .collect(Collectors.toSet());
        List<User> users = userService.listByIds(userIdSet);
        Map<Long, UserVO> solutionUserMap;
        if (!users.isEmpty()) {
            solutionUserMap = users.stream()
                    .map(UserVO::objToVo)
                    .collect(Collectors.toMap(UserVO::getId, Function.identity()));
        } else {
            solutionUserMap = new HashMap<>();
        }

        // ,
        questionSolutionList = questionSolutionList.stream()
                .peek(questionSolution -> {
//                    questionSolution.setQuestionVO(solutionQuestionMap.getOrDefault(questionSolution.getQuestionId(), null));
                    questionSolution.setUserVO(solutionUserMap.getOrDefault(questionSolution.getUserId(), null));
                })
                .collect(Collectors.toList());

        questionSolutionPage.setRecords(questionSolutionList);

        return questionSolutionPage;
    }

}




