# Code Practice — backend

Монолитное серверное приложение учебной платформы **Code Practice** (online judge + курсы + code review): REST API для SPA на Vue 3, сессии и JWT, роли `user` / `teacher` / `admin`, автоматическая проверка решений через вынесенный сервис **code-practice-sandbox**.

## Стек

- **Java 17**, **Spring Boot 3.2**, **Maven**
- **MySQL 8** + **MyBatis-Plus**, миграции **Flyway** (`src/main/resources/db/migration`)
- **Redis** — Spring Session и кэш
- **Spring Security** — cookie + JWT для API
- Загрузка файлов: **локальный диск** (`app.file`) и/или **S3-совместимое объектное хранилище** (`app.s3`, клиент по S3 API)
- Документация API: **Knife4j** (в dev-профиле)
- Тесты: **JUnit 5**, **Mockito**, **MockMvc**, **Testcontainers** (MySQL), **WireMock**; отчёт покрытия **JaCoCo**

## Требования

- JDK 17+, Maven 3.8+
- MySQL с БД `code_practice` (или свой URL в `DB_URL`)
- Redis (хост/порт в профиле или переменных `REDIS_*`)
- Для отправки решений на проверку — запущенный **code-practice-sandbox** (по умолчанию ожидается HTTP, см. `codesandbox` в `application.yml`)

## Быстрый старт (локально)

```bash
cd code-practice-backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

По умолчанию в профиле **local**: HTTP-порт **8121**, контекст **`/api`** (`server.servlet.context-path`). Фронтенд в режиме разработки проксирует запросы на этот адрес (см. `code-practice-frontend/vue.config.js`).

### MinIO (локальное S3, без отдельной установки)

В корне репозитория есть `docker-compose.yml`: MinIO на **9000** (API), консоль на **9001**, логин/пароль по умолчанию **minioadmin** / **minioadmin**, бакет **code-practice** создаётся init-контейнером (для dev на бакет выставлен публичный read).

```bash
# из корня OBIPVSIT
docker compose up -d
```

Профиль **local** в `application-local.yml` уже нацелен на `http://127.0.0.1:9000`. Данные MinIO лежат в `.minio/data` (каталог в `.gitignore`). Для продакшена эти учётные данные и публичный read не используйте.

## Конфигурация

| Область | Где настраивать |
|--------|------------------|
| Профиль Spring | `spring.profiles.active` (`local`, `prod`, `test`) |
| JDBC | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` |
| Redis | `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD` |
| S3 (прод / MinIO) | `S3_ENDPOINT`, `S3_BUCKET`, `S3_REGION`, `S3_ACCESS_KEY`, `S3_SECRET_KEY`, `S3_PUBLIC_BASE_URL`; для MinIO см. `application-local.yml` |
| Локальные файлы | `app.file.local-storage-enabled`, `app.file.local-storage-root` |
| Песочница | `codesandbox.type`, URL/секрет удалённого sandbox |

Подробности выкладки — в пояснительной записке и в `application-*.yml` (комментарии на русском/английском).

## Сборка и тесты

```bash
mvn -q clean package
mvn -q test
mvn -q jacoco:report   # отчёт: target/site/jacoco/index.html
```


## Связанные репозитории / модули

- `../code-practice-frontend` — SPA (Vue 3, Arco Design, Pinia)
- `../code-practice-sandbox` — исполнение пользовательского Java в Docker / native шаблоне
