package cn.codepractice.oj;

import cn.codepractice.oj.service.UserService;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 
 *
 */
@Tag("spring-full")
@SpringBootTest
class MainApplicationTests {

    @Resource
    private UserService userService;

    @Test
    void contextLoads() {
        assertNotNull(userService);
    }

}
