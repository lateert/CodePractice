package cn.codepractice.oj.manager;

import cn.codepractice.oj.config.AppS3Properties;
import java.io.File;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/** Загрузка файлов в объектное хранилище (по умолчанию — S3 через AWS SDK). */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final AppS3Properties props;
    private final ObjectProvider<S3Client> s3ClientProvider;

    public boolean isReady() {
        return props.isEnabled() && s3ClientProvider.getIfAvailable() != null;
    }

    /**
     * @param filepath ключ с ведущим слэшем, например {@code /user_avatar/1/a.png}
     * @return публичный URL для клиента
     */
    public String upload(String filepath, File file, String contentType) {
        S3Client client = s3ClientProvider.getIfAvailable();
        if (!props.isEnabled() || client == null) {
            throw new IllegalStateException("S3 is not configured");
        }
        String key = normalizeKey(filepath);
        String ct = contentType != null && !contentType.isBlank() ? contentType : "application/octet-stream";
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .contentType(ct)
                .build();
        client.putObject(req, RequestBody.fromFile(file));
        return publicUrl(key);
    }

    private static String normalizeKey(String filepath) {
        String fp = Objects.requireNonNullElse(filepath, "");
        return fp.startsWith("/") ? fp.substring(1) : fp;
    }

    private String publicUrl(String key) {
        String base = props.getPublicBaseUrl() == null ? "" : props.getPublicBaseUrl().trim();
        if (!base.isEmpty()) {
            return base.endsWith("/") ? base + key : base + "/" + key;
        }
        // Кастомный endpoint (MinIO и т.п.) — path-style URL
        String ep = props.getEndpoint() == null ? "" : props.getEndpoint().trim().replaceAll("/$", "");
        if (!ep.isEmpty()) {
            return ep + "/" + props.getBucket() + "/" + key;
        }
        // Для S3: URL в формате виртуального хоста с регионом
        return "https://" + props.getBucket() + ".s3." + props.getRegion() + ".amazonaws.com/" + key;
    }
}
