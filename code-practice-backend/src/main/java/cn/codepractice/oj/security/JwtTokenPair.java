package cn.codepractice.oj.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtTokenPair {

    private final String accessToken;

    private final String refreshToken;

    private final String refreshTokenId;
}
