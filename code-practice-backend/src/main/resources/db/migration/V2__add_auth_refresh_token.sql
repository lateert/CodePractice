CREATE TABLE IF NOT EXISTS auth_refresh_token
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT                             NOT NULL COMMENT 'ID пользователя',
    token_id   VARCHAR(64)                        NOT NULL COMMENT 'JTI refresh-токена',
    expires_at DATETIME                           NOT NULL COMMENT 'Срок действия refresh-токена',
    revoked    TINYINT  DEFAULT 0                 NOT NULL COMMENT 'Признак отзыва',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Время создания',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Время обновления',
    UNIQUE KEY uq_auth_refresh_token_token_id (token_id),
    INDEX idx_auth_refresh_token_user_id (user_id),
    INDEX idx_auth_refresh_token_expires_at (expires_at)
) COMMENT 'Хранилище refresh-токенов';
