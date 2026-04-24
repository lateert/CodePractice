# Code Practice — frontend

SPA для платформы **Code Practice**: задачи, отправка решений на проверку, курсы, code review. Стек: **Vue 3**, **TypeScript**, **Arco Design**, **Pinia**, **Vue Router**, **Axios**.

## Требования

- Node.js 18+ (рекомендуется LTS), npm или yarn

## Запуск в режиме разработки

```bash
cd code-practice-frontend
npm install
npm run serve
```

Прокси к API настроен в `vue.config.js` (по умолчанию на backend `http://localhost:8121/api`).

## Сборка

```bash
npm run build
```

Артефакты — в каталоге `dist/`.

## Связанные модули

- `../code-practice-backend` — REST API
- `../code-practice-sandbox` — исполнение кода
