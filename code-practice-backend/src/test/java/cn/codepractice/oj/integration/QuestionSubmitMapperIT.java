package cn.codepractice.oj.integration;

import cn.codepractice.oj.config.MyBatisPlusConfig;
import cn.codepractice.oj.mapper.QuestionSubmitMapper;
import cn.codepractice.oj.model.entity.QuestionSubmit;
import cn.codepractice.oj.model.enums.QuestionSubmitStatusEnum;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

/**
 * Интеграционный тест слоя персистентности с реальной СУБД в Docker ({@link MySQLContainer}).
 */
@Testcontainers(disabledWithoutDocker = true)
@MybatisTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(MyBatisPlusConfig.class)
class QuestionSubmitMapperIT {

    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withDatabaseName("code_practice")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("db/init-mybatis-it.sql");

    @DynamicPropertySource
    static void registerDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("mybatis-plus.configuration.map-underscore-to-camel-case", () -> true);
    }

    @Autowired
    private QuestionSubmitMapper questionSubmitMapper;

    @Test
    void insertAndSelectById() {
        QuestionSubmit row = new QuestionSubmit();
        row.setLanguage("java");
        row.setCode("public class Main {}");
        row.setJudgeInfo("{}");
        row.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        row.setQuestionId(100L);
        row.setUserId(200L);
        row.setIsDelete(0);

        int inserted = questionSubmitMapper.insert(row);
        assertTrue(inserted > 0);
        assertNotNull(row.getId());

        QuestionSubmit loaded = questionSubmitMapper.selectById(row.getId());
        assertNotNull(loaded);
        assertEqualsRow(row, loaded);
    }

    private static void assertEqualsRow(QuestionSubmit expected, QuestionSubmit actual) {
        assertNotNull(actual.getId());
        assertEquals(expected.getQuestionId(), actual.getQuestionId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals("java", actual.getLanguage());
    }
}
