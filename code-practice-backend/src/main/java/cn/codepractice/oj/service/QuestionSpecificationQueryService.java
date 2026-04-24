package cn.codepractice.oj.service;

import cn.codepractice.oj.constant.CommonConstant;
import cn.codepractice.oj.model.dto.question.QuestionQueryRequest;
import cn.codepractice.oj.model.entity.CourseQuestion;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.repository.QuestionJpaRepository;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionSpecificationQueryService {

    @Resource
    private QuestionJpaRepository questionJpaRepository;

    @Resource
    private CourseQuestionService courseQuestionService;

    public Page<Question> queryPageBySpecification(QuestionQueryRequest request, Set<Long> forcedQuestionIds) {
        if (request == null) {
            request = new QuestionQueryRequest();
        }
        long current = request.getCurrent();
        long size = request.getPageSize();

        QuestionIdRestriction restriction = resolveQuestionIdRestriction(request, forcedQuestionIds);
        if (restriction.includeIds != null && restriction.includeIds.isEmpty()) {
            Page<Question> empty = new Page<>(current, size, 0);
            empty.setRecords(Collections.emptyList());
            return empty;
        }

        Specification<Question> spec = buildSpecification(request, restriction);
        Pageable pageable = PageRequest.of((int) Math.max(0, current - 1), (int) size, buildSort(request));
        org.springframework.data.domain.Page<Question> page = questionJpaRepository.findAll(spec, pageable);
        Page<Question> mpPage = new Page<>(current, size, page.getTotalElements());
        mpPage.setRecords(page.getContent());
        return mpPage;
    }

    private Specification<Question> buildSpecification(QuestionQueryRequest request, QuestionIdRestriction restriction) {
        return (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            predicates.add(cb.equal(root.get("isDelete"), 0));
            if (request == null) {
                applyIdRestrictions(predicates, root, cb, restriction);
                return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
            }
            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (StringUtils.isNotBlank(request.getTitle())) {
                predicates.add(cb.like(root.get("title"), "%" + request.getTitle() + "%"));
            }
            if (StringUtils.isNotBlank(request.getContent())) {
                predicates.add(cb.like(root.get("content"), "%" + request.getContent() + "%"));
            }
            if (StringUtils.isNotBlank(request.getAnswer())) {
                predicates.add(cb.like(root.get("answer"), "%" + request.getAnswer() + "%"));
            }
            if (StringUtils.isNotBlank(request.getTagKeyword())) {
                predicates.add(cb.like(root.get("tags"), "%" + request.getTagKeyword() + "%"));
            }
            if (CollectionUtils.isNotEmpty(request.getTags())) {
                for (String tag : request.getTags()) {
                    predicates.add(cb.like(root.get("tags"), "%\"" + tag + "\"%"));
                }
            }
            if (request.getUserId() != null) {
                predicates.add(cb.equal(root.get("userId"), request.getUserId()));
            }
            applyIdRestrictions(predicates, root, cb, restriction);
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private QuestionIdRestriction resolveQuestionIdRestriction(QuestionQueryRequest request, Set<Long> forcedQuestionIds) {
        Set<Long> include = forcedQuestionIds == null ? null : new HashSet<>(forcedQuestionIds);
        Set<Long> exclude = null;

        if (request != null) {
            if (Boolean.TRUE.equals(request.getOnlyWithoutCourse())) {
                List<CourseQuestion> allBinds = courseQuestionService.list();
                exclude = allBinds.stream()
                        .map(CourseQuestion::getQuestionId)
                        .filter(id -> id != null && id > 0)
                        .collect(Collectors.toSet());
            } else if (request.getCourseId() != null && request.getCourseId() > 0) {
                QueryWrapper<CourseQuestion> wrapper = new QueryWrapper<>();
                wrapper.eq("course_id", request.getCourseId());
                List<CourseQuestion> list = courseQuestionService.list(wrapper);
                Set<Long> inCourse = list.stream()
                        .map(CourseQuestion::getQuestionId)
                        .filter(id -> id != null && id > 0)
                        .collect(Collectors.toSet());
                if (include == null) {
                    include = inCourse;
                } else {
                    include.retainAll(inCourse);
                }
            }
        }
        return new QuestionIdRestriction(include, exclude);
    }

    private void applyIdRestrictions(java.util.List<jakarta.persistence.criteria.Predicate> predicates,
                                     jakarta.persistence.criteria.Root<Question> root,
                                     jakarta.persistence.criteria.CriteriaBuilder cb,
                                     QuestionIdRestriction restriction) {
        if (restriction.includeIds != null) {
            predicates.add(root.get("id").in(restriction.includeIds));
        }
        if (restriction.excludeIds != null && !restriction.excludeIds.isEmpty()) {
            predicates.add(cb.not(root.get("id").in(restriction.excludeIds)));
        }
    }

    private Sort buildSort(QuestionQueryRequest request) {
        if (request == null || StringUtils.isBlank(request.getSortField())) {
            return Sort.by(Sort.Direction.DESC, "createTime");
        }
        Sort.Direction direction = CommonConstant.SORT_ORDER_ASC.equals(StringUtils.trimToEmpty(request.getSortOrder()))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        switch (request.getSortField()) {
            case "title":
                return Sort.by(direction, "title");
            case "createTime":
                return Sort.by(direction, "createTime");
            default:
                return Sort.by(Sort.Direction.DESC, "createTime");
        }
    }

    private static class QuestionIdRestriction {
        private final Set<Long> includeIds;
        private final Set<Long> excludeIds;

        private QuestionIdRestriction(Set<Long> includeIds, Set<Long> excludeIds) {
            this.includeIds = includeIds;
            this.excludeIds = excludeIds;
        }
    }
}
