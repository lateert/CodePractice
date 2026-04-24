package cn.codepractice.oj.repository;

import cn.codepractice.oj.model.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionJpaRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
}
