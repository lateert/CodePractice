package cn.codepractice.oj.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "auth.jwt")
public class AuthJwtProperties {

    private String issuer;

    private String secret;

    private long accessTokenMinutes = 30;

    private long refreshTokenDays = 14;

    private String accessCookieName = "cp_access_token";

    private String refreshCookieName = "cp_refresh_token";
}
