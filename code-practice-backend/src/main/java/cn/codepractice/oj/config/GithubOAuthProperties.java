package cn.codepractice.oj.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "integration.github.oauth")
public class GithubOAuthProperties {

    private String clientId;

    private String clientSecret;

    private String redirectUri;
}
