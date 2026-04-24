package cn.codepractice.oj.model.vo;

import lombok.Data;

/**
 * Сводные метрики по выбранному курсу.
 */
@Data
public class CourseSummaryVO {
    private Long studentCount;
    private Long questionCount;
    private Long submitTotal;
    private Long acceptedTotal;
    /** Успешность в процентах (0..100) */
    private Double successRate;
}
