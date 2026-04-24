package cn.codepractice.oj.model.dto.questionsubmit;

import cn.codepractice.oj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 
 *
 * @author peiYP
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 
     */
    private String language;

    /**
     * 
     */
    private String code;


    /**
     * 
     */
    private Integer status;


    /**
     *  id
     */
    private Long questionId;


    /**
     *  id
     */
    private Long userId;

    /**
     * Фильтр: только отправки по задачам из этого курса (course_question)
     */
    private Long courseId;

    /**
     * Подстрока в названии задачи
     */
    private String questionTitleKeyword;

    /**
     * Подстрока в логине пользователя (user_name)
     */
    private String userNameKeyword;


    private static final long serialVersionUID = 1L;
}