package cn.codepractice.oj.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class JwtTokenPayload {

    private final Long userId;

    private final String userRole;

    private final String tokenType;

    private final String tokenId;

    private final Instant expiresAt;
}
