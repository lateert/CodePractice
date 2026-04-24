package cn.codepractice.oj.model.vo;

import lombok.Data;

/**
 * Сводные метрики по задаче в рамках курса.
 */
@Data
public class QuestionSummaryVO {
    private Long questionId;
    private String questionTitle;
    private Long submitTotal;
    private Long acceptedTotal;
    /** Успешность в процентах (0..100) */
    private Double successRate;
    /** Число студентов, сделавших хотя бы одну попытку */
    private Long attemptedStudents;
    /** Число студентов, имеющих хотя бы одно успешное решение */
    private Long solvedStudents;
}
