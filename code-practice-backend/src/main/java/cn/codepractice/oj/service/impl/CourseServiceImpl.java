package cn.codepractice.oj.service.impl;

import cn.codepractice.oj.mapper.CourseMapper;
import cn.codepractice.oj.model.entity.Course;
import cn.codepractice.oj.service.CourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
        implements CourseService {
}

