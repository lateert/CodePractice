package cn.codepractice.oj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value ="question")
@Entity
@Table(name = "question")
@Data
public class Question implements Serializable {
    /** Идентификатор задачи. */
    @Id
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** Заголовок задачи. */
    private String title;

    /** Условие задачи. */
    private String content;

    /** Теги задачи в формате JSON-массива. */
    private String tags;

    /** Эталонное решение/пояснение. */
    private String answer;

    /** Количество отправок. */
    private Integer submitNum;

    /** Количество принятых решений. */
    private Integer acceptedNum;

    /** Набор тест-кейсов в формате JSON. */
    private String judgeCase;

    /** Конфиг ограничений проверки в формате JSON. */
    private String judgeConfig;

    /** Количество лайков. */
    private Integer thumbNum;

    /** Количество добавлений в избранное. */
    private Integer favourNum;

    /** Идентификатор автора задачи. */
    private Long userId;

    /** Имя автора (вычисляемое поле, не хранится в таблице). */
    @TableField(exist = false)
    @Transient
    private String userName;

    /** Время создания записи. */
    private Date createTime;

    /** Время последнего обновления записи. */
    private Date updateTime;

    /** Признак логического удаления. */
    private Integer isDelete;

    @TableField(exist = false)
    @Transient
    private static final long serialVersionUID = 1L;
}