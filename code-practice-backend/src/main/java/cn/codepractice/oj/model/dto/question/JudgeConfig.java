package cn.codepractice.oj.model.dto.question;

import lombok.Data;

@Data
public class JudgeConfig {

    /**
     *  
     */
    private Long timeLimit;

    /**
     *  KB
     */
    private Long memoryLimit;

    /**
     *  KB
     */
    private Long stackLimit;

}
