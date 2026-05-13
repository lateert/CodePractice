package cn.codepractice.oj.config;

import java.nio.file.Paths;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Локальное хранилище загрузок (без S3) */
@Data
@Component
@ConfigurationProperties(prefix = "app.file")
public class AppFileProperties {

    /** Если `true`, файлы пишутся на диск и отдаются по маршруту `/file/local/**`. */
    private boolean localStorageEnabled = false;

    /** Корневая папка для {@link #localStorageEnabled} */
    private String localStorageRoot = defaultRoot();

    private static String defaultRoot() {
        return Paths.get(System.getProperty("user.home"), ".code-practice", "local-uploads").toString();
    }
}
