package cn.codepractice.sandbox.controller;

import cn.codepractice.sandbox.JavaDockerCodeSandbox;
import cn.codepractice.sandbox.model.ExecuteCodeRequest;
import cn.codepractice.sandbox.model.ExecuteCodeResponse;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** HTTP API песочницы: health и {@code /executeCode}. */
@RestController
@RequestMapping("/")
public class MainController {

    /** Имя заголовка с общим секретом для вызова executeCode. */
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String DEFAULT_AUTH_REQUEST_SECRET = "secretKey";

    @Resource
    private JavaDockerCodeSandbox dockerCodeSandbox;


    @GetMapping("/health")
    public String healthCheck() {
        return "ok";
    }


    @PostMapping("/executeCode")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletResponse response, HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        String expectedSecret = System.getenv("SANDBOX_AUTH_SECRET");
        if (expectedSecret == null || expectedSecret.isBlank()) {
            expectedSecret = DEFAULT_AUTH_REQUEST_SECRET;
        }
        if (authHeader == null || authHeader.isBlank() || !authHeader.equals(expectedSecret)) {
            response.setStatus(403);
            return null;
        }
        if (executeCodeRequest == null) {
            throw new RuntimeException("Request body is empty");
        }
        return dockerCodeSandbox.doExecute(executeCodeRequest);
    }

}
