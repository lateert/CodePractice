package cn.codepractice.oj.integration;

import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExternalAccountRepository {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public Long findUserIdByProviderAndExternalId(String provider, String externalId) {
        List<Long> userIds = jdbcTemplate.query(
                "select user_id from external_account where provider = ? and external_id = ? limit 1",
                (rs, rowNum) -> rs.getLong("user_id"),
                provider,
                externalId
        );
        return userIds.isEmpty() ? null : userIds.get(0);
    }

    public void bind(String provider, String externalId, Long userId, String accessToken, String refreshToken) {
        try {
            jdbcTemplate.update(
                    "insert into external_account(user_id, provider, external_id, access_token, refresh_token) values(?, ?, ?, ?, ?)",
                    userId,
                    provider,
                    externalId,
                    accessToken,
                    refreshToken
            );
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Внешний аккаунт уже привязан");
        }
    }
}
