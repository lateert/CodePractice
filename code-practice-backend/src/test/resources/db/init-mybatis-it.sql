-- Схема для интеграционного теста MyBatis (Testcontainers MySQL)
CREATE TABLE IF NOT EXISTS question_submit
(
    id          bigint auto_increment comment 'id' primary key,
    language    varchar(128)                       not null comment 'Язык',
    code        text                               not null comment 'Код',
    judge_info  text                               null,
    status      int      default 0                 not null,
    question_id bigint                             not null,
    user_id     bigint                             not null,
    create_time datetime default CURRENT_TIMESTAMP not null,
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    is_delete   tinyint  default 0                 not null,
    index idx_question_id (question_id),
    index idx_user_id (user_id)
) comment 'Отправка решения' collate = utf8mb4_unicode_ci;
