package cn.codepractice.oj.model.entity;

import cn.codepractice.oj.model.vo.QuestionVO;
import cn.codepractice.oj.model.vo.UserVO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author 86188
 * @TableName question_solution
 */
@TableName(value ="question_solution")
@Data
public class QuestionSolution implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID, value = "solution_id")
    private Long solutionId;

    /**
     * 
     */
    private String solution;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    @TableField(value = "question_id")
    private Long questionId;

    /**
     * 
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 
     */
    @TableField(exist = false)
    private UserVO userVO;

    /**
     * 
     */
    private String tags;

    /**
     * 
     */
    @TableLogic
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}