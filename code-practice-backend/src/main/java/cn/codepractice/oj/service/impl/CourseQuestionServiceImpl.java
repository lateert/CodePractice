package cn.codepractice.oj.service.impl;

import cn.codepractice.oj.mapper.CourseQuestionMapper;
import cn.codepractice.oj.model.entity.CourseQuestion;
import cn.codepractice.oj.service.CourseQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CourseQuestionServiceImpl extends ServiceImpl<CourseQuestionMapper, CourseQuestion>
        implements CourseQuestionService {
}

