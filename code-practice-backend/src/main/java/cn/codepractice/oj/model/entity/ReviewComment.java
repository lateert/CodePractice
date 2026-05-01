package cn.codepractice.oj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/** Комментарий ревью к отправке решения. */
@Data
@TableName(value = "review_comment")
public class ReviewComment implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** Идентификатор отправки (`question_submit.id`). */
    private Long submissionId;

    /** Идентификатор рецензента (`user.id`). */
    private Long reviewerId;

    /**
     * Номер строки кода (опционально)
     */
    private Integer lineNumber;

    /**
     * Текст комментария
     */
    private String commentText;

    /** Статус комментария: `OPEN` / `RESOLVED`. */
    private String status;

    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

