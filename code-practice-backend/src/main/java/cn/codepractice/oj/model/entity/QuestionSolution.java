package cn.codepractice.oj.model.entity;

import cn.codepractice.oj.model.vo.QuestionVO;
import cn.codepractice.oj.model.vo.UserVO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value ="question_solution")
@Data
public class QuestionSolution implements Serializable {
    /** Идентификатор разбора. */
    @TableId(type = IdType.ASSIGN_ID, value = "solution_id")
    private Long solutionId;

    /** Текст разбора решения. */
    private String solution;

    /** Заголовок разбора. */
    private String title;

    /** Идентификатор задачи. */
    @TableField(value = "question_id")
    private Long questionId;

    /** Идентификатор автора разбора. */
    @TableField(value = "user_id")
    private Long userId;

    /** Время создания. */
    @TableField(value = "create_time")
    private Date createTime;

    /** Данные автора для выдачи в API (не хранится в таблице). */
    @TableField(exist = false)
    private UserVO userVO;

    /** Теги разбора (JSON-строка). */
    private String tags;

    /** Признак логического удаления. */
    @TableLogic
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}