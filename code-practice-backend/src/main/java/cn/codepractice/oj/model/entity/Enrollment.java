package cn.codepractice.oj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Запись пользователя на курс
 */
@Data
@TableName(value = "enrollment")
public class Enrollment implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Курс
     */
    private Long courseId;

    /**
     * Пользователь
     */
    private Long userId;

    /**
     * Дата записи
     */
    private Date enrolledAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

