package cn.codepractice.oj.service;


import cn.codepractice.oj.model.dto.question.QuestionQueryRequest;
import cn.codepractice.oj.model.dto.questionsolution.QuestionSolutionQueryRequest;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.entity.QuestionSolution;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import jakarta.servlet.http.HttpServletRequest;

public interface QuestionSolutionService extends IService<QuestionSolution> {

    QueryWrapper<QuestionSolution> getQueryWrapper(QuestionSolutionQueryRequest questionQueryRequest);

    Page<QuestionSolution> getSolutionPage(Page<QuestionSolution> questionSolutionPage, HttpServletRequest request);
}
