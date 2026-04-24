package cn.codepractice.oj.config;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

/**
 * Клиент AWS SDK v2 для любого S3-совместимого API.
 */
@Slf4j
@Configuration
public class S3ClientConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "app.s3", name = "enabled", havingValue = "true")
    public S3Client s3Client(AppS3Properties props) {
        if (props.getBucket() == null || props.getBucket().isBlank()) {
            throw new IllegalStateException("app.s3.enabled=true requires non-empty app.s3.bucket");
        }
        if (props.getAccessKey() == null || props.getSecretKey() == null
                || props.getAccessKey().isBlank() || props.getSecretKey().isBlank()) {
            throw new IllegalStateException("app.s3.enabled=true requires app.s3.access-key and app.s3.secret-key");
        }
        AwsBasicCredentials creds = AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey());
        S3Configuration s3cfg = S3Configuration.builder()
                .pathStyleAccessEnabled(props.isPathStyleAccess())
                .build();
        S3ClientBuilder builder = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .region(Region.of(props.getRegion()))
                .serviceConfiguration(s3cfg);
        if (props.getEndpoint() != null && !props.getEndpoint().isBlank()) {
            builder.endpointOverride(URI.create(props.getEndpoint().trim()));
        }
        log.info("S3 client configured: bucket={}, region={}, endpoint={}",
                props.getBucket(), props.getRegion(),
                props.getEndpoint() == null || props.getEndpoint().isBlank() ? "(default)" : props.getEndpoint());
        return builder.build();
    }
}
