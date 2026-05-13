package cn.codepractice.oj.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/** Конфигурация OpenAPI 3 и интерфейса Knife4j (только для профилей */
@Configuration
@Profile({"dev", "test", "local"})
public class Knife4jConfig {

    @Bean
    public OpenAPI codePracticeOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Code Practice API")
                        .description("Backend REST API documentation")
                        .version("1.0"));
    }
}