package cn.codepractice.oj.model.vo;

import lombok.Data;

/**
 * Элемент отчёта по успеваемости: одна задача для одного студента.
 */
@Data
public class QuestionProgressItemVO {
    private Long questionId;
    private String questionTitle;
    private Long submitCount;
    private Long acceptedCount;
}
