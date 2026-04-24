package cn.codepractice.oj.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 
 *
 * @author peiYP
 */
@Data
public class QuestionUpdateRequest implements Serializable {

    /**
     * id
     */
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