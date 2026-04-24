package cn.codepractice.oj.model.vo;

import lombok.Data;

import java.util.List;

/**
 * Успеваемость одного студента по курсу (по каждой задаче).
 */
@Data
public class StudentProgressVO {
    private Long userId;
    private String userAccount;
    private String userName;
    private Long submitTotal;
    private Long acceptedTotal;
    /** Успешность в процентах (0..100) */
    private Double successRate;
    private List<QuestionProgressItemVO> questionProgress;
}
