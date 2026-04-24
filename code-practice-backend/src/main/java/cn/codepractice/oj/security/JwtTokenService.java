package cn.codepractice.oj.security;

import cn.codepractice.oj.config.AuthJwtProperties;
import cn.codepractice.oj.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenService {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYPE = "typ";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    @Resource
    private AuthJwtProperties authJwtProperties;

    private SecretKey signingKey;

    @PostConstruct
    public void initSigningKey() {
        this.signingKey = Keys.hmacShaKeyFor(authJwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public JwtTokenPair createTokenPair(User user) {
        String refreshTokenId = UUID.randomUUID().toString().replace("-", "");
        Instant now = Instant.now();
        Instant accessExpiresAt = now.plus(authJwtProperties.getAccessTokenMinutes(), ChronoUnit.MINUTES);
        Instant refreshExpiresAt = now.plus(authJwtProperties.getRefreshTokenDays(), ChronoUnit.DAYS);

        String accessToken = Jwts.builder()
                .issuer(authJwtProperties.getIssuer())
                .subject(String.valueOf(user.getId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(accessExpiresAt))
                .claim(CLAIM_ROLE, user.getUserRole())
                .claim(CLAIM_TYPE, TOKEN_TYPE_ACCESS)
                .signWith(signingKey)
                .compact();

        String refreshToken = Jwts.builder()
                .issuer(authJwtProperties.getIssuer())
                .subject(String.valueOf(user.getId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(refreshExpiresAt))
                .id(refreshTokenId)
                .claim(CLAIM_ROLE, user.getUserRole())
                .claim(CLAIM_TYPE, TOKEN_TYPE_REFRESH)
                .signWith(signingKey)
                .compact();

        return new JwtTokenPair(accessToken, refreshToken, refreshTokenId);
    }

    public JwtTokenPayload parseAccessToken(String token) {
        return parse(token, TOKEN_TYPE_ACCESS);
    }

    public JwtTokenPayload parseRefreshToken(String token) {
        return parse(token, TOKEN_TYPE_REFRESH);
    }

    private JwtTokenPayload parse(String token, String expectedType) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String tokenType = claims.get(CLAIM_TYPE, String.class);
        if (!StringUtils.equals(tokenType, expectedType)) {
            throw new JwtException("Invalid token type");
        }
        return new JwtTokenPayload(
                Long.valueOf(claims.getSubject()),
                claims.get(CLAIM_ROLE, String.class),
                tokenType,
                claims.getId(),
                claims.getExpiration().toInstant()
        );
    }
}
