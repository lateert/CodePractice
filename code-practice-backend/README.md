# Code Practice — backend

Монолитное серверное приложение учебной платформы **Code Practice** (online judge + курсы + code review): REST API для SPA на Vue 3, сессии и JWT, роли `user` / `teacher` / `admin`, автоматическая проверка решений через вынесенный сервис **code-practice-sandbox**.

## Стек

- **Java 17**, **Spring Boot 3.2**, **Maven**
- **MySQL 8** + **MyBatis-Plus**, миграции **Flyway** (`src/main/resources/db/migration`)
- **Redis** — Spring Session и кэш
- **Spring Security** — cookie + JWT для API
- Загрузка файлов: **локальный диск** (`app.file`) и/или **Amazon S3** (`app.s3`, AWS SDK v2)
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
# пароль к MySQL лучше задать через окружение, не хранить в git:
#   PowerShell: $env:DB_PASSWORD='...'
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

По умолчанию в профиле **local**: HTTP-порт **8121**, контекст **`/api`** (`server.servlet.context-path`). Фронтенд в режиме разработки проксирует запросы на этот адрес (см. `code-practice-frontend/vue.config.js`).

## Конфигурация

| Область | Где настраивать |
|--------|------------------|
| Профиль Spring | `spring.profiles.active` (`local`, `prod`, `test`) |
| JDBC | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` |
| Redis | `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD` |
| S3 (прод) | `S3_ENABLED`, `S3_BUCKET`, `S3_REGION`, `S3_ACCESS_KEY`, `S3_SECRET_KEY`, при необходимости `S3_PUBLIC_BASE_URL` |
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
