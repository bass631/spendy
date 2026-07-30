# Spendy — Быстрый старт

## Production

Приложение развёрнуто на сервере через Nginx Proxy Manager.
Доступно по HTTPS с валидным сертификатом Let's Encrypt.

Пользователи задаются в `application-prod.yml` в секции `auth.users`.

---

## Деплой на сервер

```bash
# Из корня проекта
./deploy.sh
```

Скрипт синхронизирует файлы через rsync (по SSH) и пересобирает Docker-контейнеры spendy на сервере.

После деплоя может потребоваться очистка кеша браузера (Cmd+Shift+R).

---

## Локальная разработка

### Требования

- Java 25
- Node.js 25+
- Доступ к PostgreSQL (локально или удалённо), схема `spendy`

### 1. Backend

```bash
cd spendy-backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Доступен на http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui/index.html

> Если Java не подхватывается:
> ```bash
> export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home
> ```

### 2. Frontend

```bash
cd spendy-frontend
npm install      # только первый раз
npm run dev
```

Доступен на http://localhost:5173

Vite автоматически проксирует `/api/*` → `http://localhost:8080/api/`.

### 3. Проверка API

```bash
# Логин
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"dima","password":"dima123"}'

# Список категорий (с токеном)
curl -X GET http://localhost:8080/api/categories \
  -H "Authorization: Bearer <token>"

# Создать категорию
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"name":"Еда"}'

# Добавить расход
curl -X POST http://localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"categoryId":"<cat-uuid>","amount":1500,"description":"Обед"}'

# Статистика
curl -X GET "http://localhost:8080/api/statistics?period=month" \
  -H "Authorization: Bearer <token>"
```

### Остановка

```bash
lsof -ti:8080,5173 | xargs kill
```

---

## Управление контейнерами на сервере

```bash
ssh sandra

docker logs spendy-backend -f    # логи backend
docker logs spendy-frontend -f   # логи nginx/frontend

docker compose -f /opt/spendy/docker-compose.yml down    # остановить
docker compose -f /opt/spendy/docker-compose.yml up -d   # запустить
```

---

## Тестирование

```bash
cd spendy-backend && mvn test
cd spendy-frontend && npm test
```
