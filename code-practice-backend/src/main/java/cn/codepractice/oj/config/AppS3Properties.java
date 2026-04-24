package cn.codepractice.oj.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Объектное хранилище по протоколу S3. В продакшене по умолчанию ориентировано на Amazon S3
 * (пустой endpoint, pathStyleAccess = false). Для MinIO задают endpoint и path-style.
 * Включается при enabled = true и заданных учётных данных (см. {@link S3ClientConfiguration}).
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.s3")
public class AppS3Properties {

    /** Выкл.: используется только локальное хранилище (если оно включено) или ошибка при загрузке. */
    private boolean enabled = false;

    /**
     * Кастомный endpoint (обязателен для MinIO), например {@code http://127.0.0.1:9000}.
     * Пусто — стандартный endpoint региона AWS.
     */
    private String endpoint = "";

    private String region = "us-east-1";

    private String bucket = "";

    private String accessKey = "";

    private String secretKey = "";

    /**
     * {@code false} — стиль по умолчанию для Amazon S3 (virtual-hosted).
     * {@code true} — path-style, обычно для MinIO и кастомных S3-endpoint.
     */
    private boolean pathStyleAccess = false;

    /**
     * Публичный базовый URL для ссылок в браузере (без завершающего /). Для Amazon S3 можно оставить пустым —
     * тогда URL будет вида https://bucket.s3.region.amazonaws.com/key (нужна политика чтения объектов или CloudFront).
     */
    private String publicBaseUrl = "";
}
