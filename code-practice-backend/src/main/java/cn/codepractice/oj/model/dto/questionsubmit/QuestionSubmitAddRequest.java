package cn.codepractice.oj.model.dto.questionsubmit;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class QuestionSubmitAddRequest implements Serializable {

    /**
     * 
     */
    private String language;

    /**
     * 
     */
    private String code;


    /**
     *  id
     */
    private Long questionId;



    private static final long serialVersionUID = 1L;
}