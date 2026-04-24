-- Инициализация базы данных Code Practice (MySQL)
-- Скрипт создания схемы и таблиц приложения

-- Создать базу данных
create database if not exists code_practice;

-- Выбрать базу данных
use code_practice;

-- Таблица задач
create table if not exists question
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment 'Заголовок',
    content    text                               null comment 'Условие',
    tags       varchar(1024)                      null comment 'Список тегов (JSON-массив)',
    answer     text                               null comment 'Ответ задачи',
    submit_num  int  default 0 not null comment 'Количество отправок',
    accepted_num  int  default 0 not null comment 'Количество успешных решений',
    judge_case text null comment 'Тест-кейсы для проверки (JSON-массив)',
    judge_config text null comment 'Конфигурация проверки (JSON-объект)',
    thumb_num   int      default 0                 not null comment 'Количество лайков',
    favour_num  int      default 0                 not null comment 'Количество избранного',
    user_id     bigint                             not null comment 'ID автора',
    create_time datetime default CURRENT_TIMESTAMP not null comment 'Время создания',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Время обновления',
    is_delete   tinyint  default 0                 not null comment 'Логическое удаление',
    index idx_user_id (user_id)
) comment 'Задача' collate = utf8mb4_unicode_ci;

-- Таблица отправок решений
create table if not exists question_submit
(
    id         bigint auto_increment comment 'id' primary key,
    language   varchar(128)                       not null comment 'Язык программирования',
    code       text                               not null comment 'Код пользователя',
    judge_info  text                               null comment 'Информация о проверке (JSON-объект)',
    status     int      default 0                 not null comment 'Статус проверки (0 - в очереди, 1 - проверяется, 2 - успешно, 3 - ошибка)',
    question_id bigint                             not null comment 'ID задачи',
    user_id     bigint                             not null comment 'ID пользователя',
    create_time datetime default CURRENT_TIMESTAMP not null comment 'Время создания',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Время обновления',
    is_delete   tinyint  default 0                 not null comment 'Логическое удаление',
    index idx_question_id (question_id),
    index idx_user_id (user_id)
) comment 'Отправка решения';

CREATE TABLE question_solution (
    solution_id BIGINT PRIMARY KEY COMMENT 'Первичный ключ',
    solution TEXT COMMENT 'Содержание разбора',
    question_id BIGINT COMMENT 'ID задачи',
    user_id BIGINT COMMENT 'ID автора',
    create_time DATETIME COMMENT 'Время создания',
    is_delete INT COMMENT 'Логическое удаление',
    title VARCHAR(255) COMMENT 'Заголовок',
    tags JSON COMMENT 'Теги задачи',
    index idx_questionId (question_id),
    index idx_userId (user_id)
) COMMENT 'Таблица разборов';


-- Таблица пользователей
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    user_account  varchar(256)                           not null comment 'Логин',
    user_password varchar(512)                           not null comment 'Пароль',
    union_id      varchar(256)                           null comment 'ID в WeChat Open Platform',
    mp_open_id     varchar(256)                           null comment 'OpenId публичного аккаунта',
    user_name     varchar(256)                           null comment 'Имя пользователя',
    user_avatar   varchar(1024)                          null comment 'Аватар пользователя',
    user_profile  varchar(512)                           null comment 'Профиль пользователя',
    user_role     varchar(256) default 'user'            not null comment 'Роль пользователя: user/admin/ban',
    create_time   datetime     default CURRENT_TIMESTAMP not null comment 'Время создания',
    update_time   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Время обновления',
    is_delete     tinyint      default 0                 not null comment 'Логическое удаление',
    index idx_union_id (union_id)
) comment 'Пользователь' collate = utf8mb4_unicode_ci;

-- Таблица привязки внешних аккаунтов (OAuth2)
create table if not exists external_account
(
    id            bigint auto_increment comment 'id' primary key,
    user_id       bigint                             not null comment 'ID локального пользователя',
    provider      varchar(64)                        not null comment 'Провайдер, например github / google',
    external_id   varchar(256)                       not null comment 'ID пользователя во внешней системе',
    access_token  text                               null comment 'Токен доступа',
    refresh_token text                               null comment 'Токен обновления',
    create_time   datetime default CURRENT_TIMESTAMP not null comment 'Время создания',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Время обновления',
    unique key uq_provider_external (provider, external_id),
    index idx_external_user_id (user_id)
) comment 'Привязка внешних аккаунтов';

-- Таблица тегов
create table if not exists tag
(
    id          bigint auto_increment comment 'id' primary key,
    name        varchar(64)                        not null comment 'Название тега',
    create_time datetime default CURRENT_TIMESTAMP not null comment 'Время создания',
    unique key uq_tag_name (name)
) comment 'Тег';

-- Таблица связи задач и тегов
create table if not exists question_tag
(
    question_id bigint not null comment 'ID задачи',
    tag_id      bigint not null comment 'ID тега',
    primary key (question_id, tag_id),
    constraint fk_question_tag_question foreign key (question_id) references question (id) on delete cascade,
    constraint fk_question_tag_tag foreign key (tag_id) references tag (id) on delete cascade
) comment 'Связь задача-тег';

-- Таблица курсов
create table if not exists course
(
    id           bigint auto_increment comment 'id' primary key,
    title        varchar(255)                       not null comment 'Название курса',
    description  text                               null comment 'Описание курса (Markdown)',
    author_id    bigint                             not null comment 'ID автора',
    is_published tinyint  default 0                 not null comment 'Публикация: 0 - черновик, 1 - опубликован',
    create_time  datetime default CURRENT_TIMESTAMP not null comment 'Время создания',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Время обновления',
    index idx_course_author_id (author_id)
) comment 'Курс';

-- Таблица связи курсов и задач
create table if not exists course_question
(
    course_id  bigint not null comment 'ID курса',
    question_id bigint not null comment 'ID задачи',
    position   int    not null default 0 comment 'Порядок в курсе',
    primary key (course_id, question_id),
    constraint fk_course_question_course foreign key (course_id) references course (id) on delete cascade,
    constraint fk_course_question_question foreign key (question_id) references question (id) on delete cascade
) comment 'Связь курс-задача';

-- Таблица записей на курс
create table if not exists enrollment
(
    id          bigint auto_increment comment 'id' primary key,
    course_id   bigint                             not null comment 'ID курса',
    user_id     bigint                             not null comment 'ID пользователя',
    enrolled_at datetime default CURRENT_TIMESTAMP not null comment 'Время записи',
    unique key uq_enrollment_course_user (course_id, user_id),
    index idx_enrollment_user_id (user_id)
) comment 'Запись на курс';

-- Таблица комментариев code review к отправкам
create table if not exists review_comment
(
    id            bigint auto_increment comment 'id' primary key,
    submission_id bigint                             not null comment 'ID отправки (question_submit.id)',
    reviewer_id   bigint                             not null comment 'ID ревьюера',
    line_number   int                                null comment 'Номер строки кода (опционально)',
    comment_text  text                               not null comment 'Текст комментария',
    status        varchar(32) default 'OPEN'         not null comment 'Статус: OPEN / RESOLVED',
    create_time   datetime default CURRENT_TIMESTAMP not null comment 'Время создания',
    index idx_review_submission_id (submission_id),
    index idx_review_reviewer_id (reviewer_id)
) comment 'Комментарии review к отправкам';

