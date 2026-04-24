package cn.codepractice.oj.model.dto.course;

import lombok.Data;

import java.io.Serializable;

/**
 * Запись / отписка от курса
 */
@Data
public class CourseEnrollRequest implements Serializable {

    /**
     * Идентификатор курса
     */
    private Long courseId;

    private static final long serialVersionUID = 1L;
}

