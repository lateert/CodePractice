package cn.codepractice.oj.integration;

import cn.codepractice.oj.MainApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Поднимает приложение с MySQL и Redis в Docker, запрашивает SpringDoc OpenAPI и сохраняет JSON
 * в {@code code-practice-frontend/openapi.json} для {@code npm run generate:api:file}.
 * <p>
 * Запуск (из каталога {@code code-practice-backend}): {@code mvn test -Dtest=OpenApiExportIT}
 * <p>
 * Без Docker демона тест пропускается ({@code disabledWithoutDocker = true}).
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = MainApplication.class,
        properties = {
                "spring.autoconfigure.exclude="
                        + "org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration"
        })
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
class OpenApiExportIT {

    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withDatabaseName("code_practice")
            .withUsername("root")
            .withPassword("test");

    @Container
    private static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> String.valueOf(REDIS.getMappedPort(6379)));
        registry.add("spring.data.redis.password", () -> "");
        registry.add("spring.data.redis.database", () -> "1");
        registry.add("spring.session.store-type", () -> "redis");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void exportOpenApiJsonForFrontendCodegen() throws Exception {
        String url = "http://127.0.0.1:" + port + "/api/v3/api-docs";
        String body = restTemplate.getForObject(url, String.class);
        Assertions.assertNotNull(body, "OpenAPI body");
        Assertions.assertTrue(
                body.contains("\"openapi\"") || body.contains("\"swagger\""),
                "Expected OpenAPI/Swagger JSON");

        Path backendDir = Path.of(System.getProperty("user.dir"));
        Path out = backendDir.resolve("../code-practice-frontend/openapi.json").normalize();
        Files.createDirectories(out.getParent());
        Files.writeString(out, body, StandardCharsets.UTF_8);
    }
}
