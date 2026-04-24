package cn.codepractice.oj.security;

import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;

@Repository
public class RefreshTokenRepository {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public void save(Long userId, String tokenId, Instant expiresAt) {
        jdbcTemplate.update(
                "insert into auth_refresh_token(user_id, token_id, expires_at, revoked) values(?, ?, ?, 0)",
                userId,
                tokenId,
                Timestamp.from(expiresAt)
        );
    }

    public boolean isValid(Long userId, String tokenId) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from auth_refresh_token where user_id = ? and token_id = ? and revoked = 0 and expires_at > now()",
                Integer.class,
                userId,
                tokenId
        );
        return count != null && count > 0;
    }

    public void revoke(String tokenId) {
        jdbcTemplate.update(
                "update auth_refresh_token set revoked = 1 where token_id = ?",
                tokenId
        );
    }

    public void revokeAllByUserId(Long userId) {
        jdbcTemplate.update(
                "update auth_refresh_token set revoked = 1 where user_id = ? and revoked = 0",
                userId
        );
    }

    public void cleanupExpired() {
        jdbcTemplate.update("delete from auth_refresh_token where expires_at <= now()");
    }
}
