package cn.codepractice.oj.model.dto.questionsolution;

import cn.codepractice.oj.common.PageRequest;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionSolutionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long solutionId;

    /**
     * 
     */
    private String title;

    /**
     *  id
     */
    private Long userId;

    /**
     * 
     */
    private Long questionId;


    private static final long serialVersionUID = 1L;
}