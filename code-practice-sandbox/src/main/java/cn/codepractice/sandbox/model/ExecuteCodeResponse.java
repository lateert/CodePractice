package cn.codepractice.sandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author peiYP
 * @create 2023-12-31 17:56
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {

    /**
     * 
     */
    private String message;

    /**
     * 
     */
    private List<String> output;

    /**
     * 
     */
    private JudgeInfo judgeInfo;

    /**
     * 
     */
    private Integer status;

    // Явные геттеры/сеттеры, чтобы не зависеть от Lombok при компиляции

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getOutput() {
        return output;
    }

    public void setOutput(List<String> output) {
        this.output = output;
    }

    public JudgeInfo getJudgeInfo() {
        return judgeInfo;
    }

    public void setJudgeInfo(JudgeInfo judgeInfo) {
        this.judgeInfo = judgeInfo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
