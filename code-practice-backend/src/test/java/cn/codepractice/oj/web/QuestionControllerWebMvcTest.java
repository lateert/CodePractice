package cn.codepractice.oj.web;

import cn.codepractice.oj.controller.QuestionController;
import cn.codepractice.oj.exception.GlobalExceptionHandler;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.service.CourseQuestionService;
import cn.codepractice.oj.service.EnrollmentService;
import cn.codepractice.oj.service.QuestionService;
import cn.codepractice.oj.service.QuestionSubmitService;
import cn.codepractice.oj.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Проверка REST-слоя через {@link MockMvc} (standalone + моки сервисов), без поднятия Spring Boot целиком.
 */
@ExtendWith(MockitoExtension.class)
class QuestionControllerWebMvcTest {

    @Mock
    private QuestionSubmitService questionSubmitService;
    @Mock
    private QuestionService questionService;
    @Mock
    private UserService userService;
    @Mock
    private EnrollmentService enrollmentService;
    @Mock
    private CourseQuestionService courseQuestionService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        QuestionController controller = new QuestionController();
        ReflectionTestUtils.setField(controller, "questionSubmitService", questionSubmitService);
        ReflectionTestUtils.setField(controller, "questionService", questionService);
        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "enrollmentService", enrollmentService);
        ReflectionTestUtils.setField(controller, "courseQuestionService", courseQuestionService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void submit_invalidQuestionId_returnsBusinessError() throws Exception {
        String body = "{\"questionId\":0,\"language\":\"java\",\"code\":\"x\"}";
        mockMvc.perform(post("/question/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40000));
    }

    @Test
    void submit_ok_returnsData() throws Exception {
        User u = new User();
        u.setId(1L);
        when(userService.getLoginUser(any(HttpServletRequest.class))).thenReturn(u);
        when(questionSubmitService.doQuestionSubmit(any(), eq(u))).thenReturn(77L);

        String body = "{\"questionId\":10,\"language\":\"java\",\"code\":\"class Main {}\"}";
        mockMvc.perform(post("/question/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(77));
    }

    @Test
    void codeTemplate_delegatesToService() throws Exception {
        when(questionService.getCodeTemplate(any())).thenReturn("template");
        String body = "{\"language\":\"java\"}";
        mockMvc.perform(post("/question/codeTemplate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("template"));
    }
}
