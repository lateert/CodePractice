package cn.codepractice.oj.model.dto.course;

import lombok.Data;

import java.io.Serializable;

/**
 * Привязка задачи к курсу
 */
@Data
public class CourseQuestionBindRequest implements Serializable {

    /**
     * Идентификатор курса
     */
    private Long courseId;

    /**
     * Идентификатор задачи
     */
    private Long questionId;

    /**
     * Позиция задачи в курсе (необязательно)
     */
    private Integer position;

    private static final long serialVersionUID = 1L;
}

