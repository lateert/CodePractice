package cn.codepractice.oj.judege.codesandbox.model;

import lombok.Data;

@Data
public class JudgeInfo {

    private String message;

    private Long memory;

    private Long time;

    private Integer status;

}
