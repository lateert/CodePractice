package cn.codepractice.oj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value ="question_submit")
public class QuestionSubmit implements Serializable {
    /** Идентификатор отправки. */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** Язык программирования отправки. */
    private String language;

    /** Исходный код пользователя. */
    private String code;

    /** Детали результата проверки в формате JSON. */
    private String judgeInfo;

    /** Статус проверки (коды из QuestionSubmitStatusEnum). */
    private Integer status;

    /** Идентификатор задачи. */
    private Long questionId;

    /** Идентификатор пользователя. */
    private Long userId;

    /** Время создания записи. */
    private Date createTime;

    /** Время последнего обновления записи. */
    private Date updateTime;

    /** Признак логического удаления. */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}