package cn.codepractice.oj.model.dto.course;

import lombok.Data;

import java.io.Serializable;

@Data
public class CoursePublishRequest implements Serializable {

    private Long courseId;

    /** true — опубликовать курс, false — закрыть доступ. */
    private Boolean published;

    private static final long serialVersionUID = 1L;
}
