package cn.codepractice.oj.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Раздача локально загруженных файлов (без S3). */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.file.local-storage-enabled", havingValue = "true")
public class LocalUploadWebMvcConfig implements WebMvcConfigurer {

    private final AppFileProperties appFileProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path root = Paths.get(appFileProperties.getLocalStorageRoot()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create local upload root: " + root, e);
        }
        String location = root.toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        log.info("Local file uploads served from {} as /file/local/**", root);
        registry.addResourceHandler("/file/local/**").addResourceLocations(location);
    }
}
