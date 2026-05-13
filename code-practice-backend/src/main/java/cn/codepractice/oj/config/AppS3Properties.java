package cn.codepractice.oj.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.s3")
public class AppS3Properties {

    private boolean enabled = false;

    /**
     * Кастомный endpoint (обязателен для MinIO), например {@code http://127.0.0.1:9000}.
     * Пусто — endpoint по умолчанию для выбранного региона (облачный S3 без custom URL).
     */
    private String endpoint = "";

    private String region = "us-east-1";

    private String bucket = "";

    private String accessKey = "";

    private String secretKey = "";

    private boolean pathStyleAccess = false;

    /**
     * Публичный базовый URL для ссылок в браузере (без завершающего /)
     */
    private String publicBaseUrl = "";
}
