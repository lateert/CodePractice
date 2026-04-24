# Code Practice — sandbox

Небольшой **Spring Boot**‑сервис: принимает JSON с кодом и входами, компилирует и запускает **Java** в изолированной среде (**Docker** или шаблон на локальном `javac`/`java` — см. реализации в пакете `cn.codepractice.sandbox`).

## Запуск

- Нужен **JDK 17+** и (для Docker‑режима) **Docker Engine** с доступом API (например `DOCKER_HOST=tcp://localhost:2375` на Windows при включённом «Expose daemon»).
- Секрет заголовка авторизации: переменная **`SANDBOX_AUTH_SECRET`** (если не задана — используется значение по умолчанию из кода только для локальных тестов; в продакшене задайте явно).

```bash
cd code-practice-sandbox
mvn spring-boot:run
```

Эндпоинты: `GET /health`, `POST /executeCode` (тело — `ExecuteCodeRequest`, заголовок `auth` = секрет).

## Связь с backend

Основное приложение **code-practice-backend** вызывает sandbox по HTTP; URL и тип песочницы задаются в `application.yml` (`codesandbox`).
