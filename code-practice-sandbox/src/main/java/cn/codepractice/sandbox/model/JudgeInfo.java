package cn.codepractice.sandbox.model;

import lombok.Data;

@Data
public class JudgeInfo {

    private String message;

    private Long memory;

    private Long time;

    // Явные геттеры/сеттеры, чтобы не зависеть от Lombok при компиляции

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getMemory() {
        return memory;
    }

    public void setMemory(Long memory) {
        this.memory = memory;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}
