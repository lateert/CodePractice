package cn.codepractice.oj.model.dto.course;

import lombok.Data;

import java.io.Serializable;

@Data
public class CourseUpdateRequest implements Serializable {

    private Long id;

    private String title;

    private String description;

    /**
     * Признак публикации
     */
    private Boolean published;

    private static final long serialVersionUID = 1L;
}
