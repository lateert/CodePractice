package cn.codepractice.oj.model.dto.questionsolution;

import cn.codepractice.oj.model.dto.question.JudgeCase;
import cn.codepractice.oj.model.dto.question.JudgeConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuestionSolutionAddRequest implements Serializable {

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private String solution;

    /**
     * id
     */
    private Long questionId;

    /**
     * 
     */
    private List<String> tags;



    private static final long serialVersionUID = 1L;
}