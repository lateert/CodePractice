package cn.codepractice.oj.service.impl;

import cn.codepractice.oj.mapper.EnrollmentMapper;
import cn.codepractice.oj.model.entity.Enrollment;
import cn.codepractice.oj.service.EnrollmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentServiceImpl extends ServiceImpl<EnrollmentMapper, Enrollment>
        implements EnrollmentService {
}

