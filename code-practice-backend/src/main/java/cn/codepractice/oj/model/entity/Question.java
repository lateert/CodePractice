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

/**
 * 
 * @TableName question
 */
@TableName(value ="question")
@Entity
@Table(name = "question")
@Data
public class Question implements Serializable {
    /**
     * id
     */
    @Id
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
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
    private String tags;

    /**
     * 
     */
    private String answer;

    /**
     * 
     */
    private Integer submitNum;

    /**
     * 
     */
    private Integer acceptedNum;

    /**
     * （json ）
     */
    private String judgeCase;

    /**
     * （json ）
     */
    private String judgeConfig;

    /**
     * 
     */
    private Integer thumbNum;

    /**
     * 
     */
    private Integer favourNum;

    /**
     *  id
     */
    private Long userId;

    /**
     * 
     */
    @TableField(exist = false)
    @Transient
    private String userName;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private Integer isDelete;

    @TableField(exist = false)
    @Transient
    private static final long serialVersionUID = 1L;
}