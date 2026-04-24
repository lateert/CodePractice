package cn.codepractice.oj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author peiyp
 * @TableName question_submit
 */
@Data
@TableName(value ="question_submit")
public class QuestionSubmit implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 
     */
    private String language;

    /**
     * 
     */
    private String code;

    /**
     * （json ）
     */
    private String judgeInfo;

    /**
     * （0 - 、1 - 、2 - 、3 - ）
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
    private static final long serialVersionUID = 1L;

}