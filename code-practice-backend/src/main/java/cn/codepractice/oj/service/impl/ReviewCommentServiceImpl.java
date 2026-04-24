package cn.codepractice.oj.service.impl;

import cn.codepractice.oj.mapper.ReviewCommentMapper;
import cn.codepractice.oj.model.entity.ReviewComment;
import cn.codepractice.oj.service.ReviewCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ReviewCommentServiceImpl extends ServiceImpl<ReviewCommentMapper, ReviewComment>
        implements ReviewCommentService {
}

