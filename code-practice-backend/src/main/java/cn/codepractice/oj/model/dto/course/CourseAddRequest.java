package cn.codepractice.oj.model.dto.course;

import lombok.Data;

import java.io.Serializable;

@Data
public class CourseAddRequest implements Serializable {

    private String title;

    private String description;

    /**
     * Признак публикации при создании (по умолчанию false)
     */
    private Boolean published;

    private static final long serialVersionUID = 1L;
}

