package cn.codepractice.oj.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 
 *
 * @author peiYP
 */
@Data
public class QuestionAddRequest implements Serializable {

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
    private List<String> tags;

    /**
     * 
     */
    private String answer;


    /**
     * （json ）
     */
    private List<JudgeCase> judgeCase;

    /**
     * （json ）
     */
    private JudgeConfig judgeConfig;



    private static final long serialVersionUID = 1L;
}