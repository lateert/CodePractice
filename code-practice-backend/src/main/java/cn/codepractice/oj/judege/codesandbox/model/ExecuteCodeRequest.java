package cn.codepractice.oj.judege.codesandbox.model;

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
public class ExecuteCodeRequest {

    /**
     * 
     */
    private String code;

    /**
     * 
     */
    private String language;

    /**
     * 
     */
    private List<String> input;

}
