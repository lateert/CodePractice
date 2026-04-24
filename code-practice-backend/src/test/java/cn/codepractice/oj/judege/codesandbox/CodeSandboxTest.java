package cn.codepractice.oj.judege.codesandbox;

import cn.codepractice.oj.judege.codesandbox.impl.ExampleCodeSandbox;
import cn.codepractice.oj.judege.codesandbox.model.ExecuteCodeRequest;
import cn.codepractice.oj.judege.codesandbox.model.ExecuteCodeResponse;
import cn.codepractice.oj.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


class CodeSandboxTest {

    @Test
    void executeCode() {
        ExampleCodeSandbox codeSandbox = new ExampleCodeSandbox();
        String code = "int main() { }";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .input(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.doExecute(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }


    @Test
    void executeCodeProxy() {
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance("example");
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String code = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(\"result:\" + (a + b));\n" +
                "    }\n" +
                "}";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .input(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.doExecute(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);

    }

}