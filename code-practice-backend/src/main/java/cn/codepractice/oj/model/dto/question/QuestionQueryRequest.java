package cn.codepractice.oj.model.dto.question;

import cn.codepractice.oj.common.PageRequest;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 
 *
 * @author peiYP
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private String content;

    /**
     * （json ）
     */
    private List<String> tags;

    /**
     * 
     */
    private String answer;

    /**
     *  id
     */
    private Long userId;

    /**
     * Фильтр по курсу: только задачи, привязанные к этому courseId (игнорируется, если onlyWithoutCourse = true).
     */
    private Long courseId;

    /**
     * Только задачи без привязки ни к одному курсу.
     */
    private Boolean onlyWithoutCourse;

    /**
     * Подстрока в JSON-тегах (частичное совпадение).
     */
    private String tagKeyword;

    private static final long serialVersionUID = 1L;
}