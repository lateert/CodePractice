CREATE TABLE IF NOT EXISTS question
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    title         VARCHAR(512)                       NULL COMMENT 'Заголовок',
    content       TEXT                               NULL COMMENT 'Условие',
    tags          VARCHAR(1024)                      NULL COMMENT 'Список тегов (JSON-массив)',
    answer        TEXT                               NULL COMMENT 'Ответ задачи',
    submit_num    INT      DEFAULT 0                 NOT NULL COMMENT 'Количество отправок',
    accepted_num  INT      DEFAULT 0                 NOT NULL COMMENT 'Количество успешных решений',
    judge_case    TEXT                               NULL COMMENT 'Тест-кейсы для проверки (JSON-массив)',
    judge_config  TEXT                               NULL COMMENT 'Конфигурация проверки (JSON-объект)',
    thumb_num     INT      DEFAULT 0                 NOT NULL COMMENT 'Количество лайков',
    favour_num    INT      DEFAULT 0                 NOT NULL COMMENT 'Количество избранного',
    user_id       BIGINT                             NOT NULL COMMENT 'ID автора',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Время создания',
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Время обновления',
    is_delete     TINYINT  DEFAULT 0                 NOT NULL COMMENT 'Логическое удаление',
    INDEX idx_user_id (user_id)
) COMMENT 'Задача' COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS question_submit
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    language    VARCHAR(128)                       NOT NULL COMMENT 'Язык программирования',
    code        TEXT                               NOT NULL COMMENT 'Код пользователя',
    judge_info  TEXT                               NULL COMMENT 'Информация о проверке (JSON-объект)',
    status      INT      DEFAULT 0                 NOT NULL COMMENT 'Статус проверки',
    question_id BIGINT                             NOT NULL COMMENT 'ID задачи',
    user_id     BIGINT                             NOT NULL COMMENT 'ID пользователя',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Время создания',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Время обновления',
    is_delete   TINYINT  DEFAULT 0                 NOT NULL COMMENT 'Логическое удаление',
    INDEX idx_question_id (question_id),
    INDEX idx_user_id (user_id)
) COMMENT 'Отправка решения';

CREATE TABLE IF NOT EXISTS question_solution
(
    solution_id BIGINT PRIMARY KEY COMMENT 'Первичный ключ',
    solution    TEXT         NULL COMMENT 'Содержание разбора',
    question_id BIGINT       NULL COMMENT 'ID задачи',
    user_id     BIGINT       NULL COMMENT 'ID автора',
    create_time DATETIME     NULL COMMENT 'Время создания',
    is_delete   INT          NULL COMMENT 'Логическое удаление',
    title       VARCHAR(255) NULL COMMENT 'Заголовок',
    tags        JSON         NULL COMMENT 'Теги задачи',
    INDEX idx_questionId (question_id),
    INDEX idx_userId (user_id)
) COMMENT 'Таблица разборов';

CREATE TABLE IF NOT EXISTS user
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    user_account  VARCHAR(256)                           NOT NULL COMMENT 'Логин',
    user_password VARCHAR(512)                           NOT NULL COMMENT 'Пароль',
    union_id      VARCHAR(256)                           NULL COMMENT 'ID в WeChat Open Platform',
    mp_open_id    VARCHAR(256)                           NULL COMMENT 'OpenId публичного аккаунта',
    user_name     VARCHAR(256)                           NULL COMMENT 'Имя пользователя',
    user_avatar   VARCHAR(1024)                          NULL COMMENT 'Аватар пользователя',
    user_profile  VARCHAR(512)                           NULL COMMENT 'Профиль пользователя',
    user_role     VARCHAR(256) DEFAULT 'user'            NOT NULL COMMENT 'Роль пользователя: user/admin/teacher/ban',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Время создания',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Время обновления',
    is_delete     TINYINT      DEFAULT 0                 NOT NULL COMMENT 'Логическое удаление',
    INDEX idx_union_id (union_id)
) COMMENT 'Пользователь' COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS external_account
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    user_id       BIGINT                             NOT NULL COMMENT 'ID локального пользователя',
    provider      VARCHAR(64)                        NOT NULL COMMENT 'Провайдер, например github / google',
    external_id   VARCHAR(256)                       NOT NULL COMMENT 'ID пользователя во внешней системе',
    access_token  TEXT                               NULL COMMENT 'Токен доступа',
    refresh_token TEXT                               NULL COMMENT 'Токен обновления',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Время создания',
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Время обновления',
    UNIQUE KEY uq_provider_external (provider, external_id),
    INDEX idx_external_user_id (user_id)
) COMMENT 'Привязка внешних аккаунтов';

CREATE TABLE IF NOT EXISTS tag
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    name        VARCHAR(64)                        NOT NULL COMMENT 'Название тега',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Время создания',
    UNIQUE KEY uq_tag_name (name)
) COMMENT 'Тег';

CREATE TABLE IF NOT EXISTS question_tag
(
    question_id BIGINT NOT NULL COMMENT 'ID задачи',
    tag_id      BIGINT NOT NULL COMMENT 'ID тега',
    PRIMARY KEY (question_id, tag_id),
    CONSTRAINT fk_question_tag_question FOREIGN KEY (question_id) REFERENCES question (id) ON DELETE CASCADE,
    CONSTRAINT fk_question_tag_tag FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
) COMMENT 'Связь задача-тег';

CREATE TABLE IF NOT EXISTS course
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    title        VARCHAR(255)                       NOT NULL COMMENT 'Название курса',
    description  TEXT                               NULL COMMENT 'Описание курса (Markdown)',
    author_id    BIGINT                             NOT NULL COMMENT 'ID автора',
    is_published TINYINT  DEFAULT 0                 NOT NULL COMMENT 'Публикация: 0 - черновик, 1 - опубликован',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Время создания',
    update_time  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Время обновления',
    INDEX idx_course_author_id (author_id)
) COMMENT 'Курс';

CREATE TABLE IF NOT EXISTS course_question
(
    course_id   BIGINT NOT NULL COMMENT 'ID курса',
    question_id BIGINT NOT NULL COMMENT 'ID задачи',
    position    INT    NOT NULL DEFAULT 0 COMMENT 'Порядок в курсе',
    PRIMARY KEY (course_id, question_id),
    CONSTRAINT fk_course_question_course FOREIGN KEY (course_id) REFERENCES course (id) ON DELETE CASCADE,
    CONSTRAINT fk_course_question_question FOREIGN KEY (question_id) REFERENCES question (id) ON DELETE CASCADE
) COMMENT 'Связь курс-задача';

CREATE TABLE IF NOT EXISTS enrollment
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    course_id   BIGINT                             NOT NULL COMMENT 'ID курса',
    user_id     BIGINT                             NOT NULL COMMENT 'ID пользователя',
    enrolled_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Время записи',
    UNIQUE KEY uq_enrollment_course_user (course_id, user_id),
    INDEX idx_enrollment_user_id (user_id)
) COMMENT 'Запись на курс';

CREATE TABLE IF NOT EXISTS review_comment
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    submission_id BIGINT                             NOT NULL COMMENT 'ID отправки (question_submit.id)',
    reviewer_id   BIGINT                             NOT NULL COMMENT 'ID ревьюера',
    line_number   INT                                NULL COMMENT 'Номер строки кода (опционально)',
    comment_text  TEXT                               NOT NULL COMMENT 'Текст комментария',
    status        VARCHAR(32) DEFAULT 'OPEN'         NOT NULL COMMENT 'Статус: OPEN / RESOLVED',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Время создания',
    INDEX idx_review_submission_id (submission_id),
    INDEX idx_review_reviewer_id (reviewer_id)
) COMMENT 'Комментарии review к отправкам';
