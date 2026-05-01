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


@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    /** Идентификатор задачи. */
    private Long id;

    /** Подстрока в заголовке. */
    private String title;

    /** Подстрока в условии задачи. */
    private String content;

    /** Фильтр по тегам. */
    private List<String> tags;

    /** Подстрока в ответе/пояснении. */
    private String answer;

    /** Идентификатор автора задачи. */
    private Long userId;

    /** Фильтр по курсу: только задачи, привязанные к `courseId`. */
    private Long courseId;

    /** Только задачи без привязки к курсам. */
    private Boolean onlyWithoutCourse;

    /** Подстрока для поиска в тегах. */
    private String tagKeyword;

    private static final long serialVersionUID = 1L;
}