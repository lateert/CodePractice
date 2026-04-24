package cn.codepractice.oj.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * Связь курс - задача
 */
@Data
@TableName(value = "course_question")
public class CourseQuestion implements Serializable {

    /**
     * Идентификатор курса
     */
    private Long courseId;

    /**
     * Идентификатор задачи
     */
    private Long questionId;

    /**
     * Позиция задачи в курсе
     */
    private Integer position;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

