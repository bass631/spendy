<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://github.com/user-attachments/assets/header-dark.svg">
  <img alt="Spendy" src="https://github.com/user-attachments/assets/header-light.svg">
</picture>

<p align="center">
  <strong>Личное веб-приложение для учёта расходов</strong><br>
  Минималистичный трекер трат с категориями, статистикой и XLSX-экспортом.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-%23ED8B00?logo=openjdk&logoColor=white" alt="Java 25">
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.3-%236DB33F?logo=springboot&logoColor=white" alt="Spring Boot 4.0.3">
  <img src="https://img.shields.io/badge/Vue.js-3.5-%234FC08D?logo=vuedotjs&logoColor=white" alt="Vue 3.5">
  <img src="https://img.shields.io/badge/Vite-6.0-%23646CFF?logo=vite&logoColor=white" alt="Vite 6">
  <img src="https://img.shields.io/badge/PostgreSQL-17-%234169E1?logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Docker-compose-%232496ED?logo=docker&logoColor=white" alt="Docker">
</p>

---

## Возможности

- **Учёт расходов** — нажал на кнопку категории, ввёл сумму, готово
- **Категории** отсортированы по частоте использования за неделю
- **Статистика** — круговая диаграмма и таблица за день / неделю / месяц / год / произвольный период
- **XLSX-экспорт** — выгрузка отфильтрованной статистики в Excel
- **Mobile-first** адаптивный дизайн с bottom-sheet модалками, touch-friendly, поддержка iOS safe-area
- **In-memory аутентификация** — JWT, без регистрации, пользователи задаются в YAML
- **PWA-ready** — service worker генерируется в production-сборке
- **Docker** — multi-stage сборка, Compose оркестрация, Nginx reverse proxy

---

## Технологический стек

### Backend — `spendy-backend/`

| Компонент | Версия |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.3 |
| Spring Security + JWT | jjwt 0.12.6 |
| Spring Data JPA / Hibernate | 7.x |
| PostgreSQL Driver | – |
| Flyway | 11.x |
| MapStruct | 1.6.3 |
| Lombok | – |
| SpringDoc OpenAPI | 3.0.3 |
| Apache POI (XLSX) | 5.3.0 |
| Testcontainers | 1.20.4 |
| Maven | – |

### Frontend — `spendy-frontend/`

| Компонент | Версия |
|---|---|
| Vue.js | 3.5.x |
| Vite | 6.x |
| Vue Router | 4.5.x |
| Pinia | 2.3.x |
| TypeScript | 5.7.x |
| Axios + axios-retry | 1.7.x / 4.5.x |
| Chart.js + vue-chartjs | 4.4.x / 5.3.x |
| Vitest + Vue Test Utils | 3.x / 2.4.x |

### Инфраструктура

| Компонент | Роль |
|---|---|
| PostgreSQL 17 | База данных |
| Docker + Compose | Оркестрация контейнеров |
| Nginx | Раздача SPA + прокси `/api/` |
| Nginx Proxy Manager | SSL termination (production) |

---

## Архитектура

```
spendy/
├── spendy-backend/          # Java 25 + Spring Boot 4 REST API
│   ├── src/main/java/.../
│   │   ├── config/          # Security, Flyway, OpenAPI
│   │   ├── security/        # JWT фильтр + utils, in-memory users
│   │   ├── controller/      # Auth, Category, Expense, Statistics
│   │   ├── service/         # Бизнес-логика (интерфейс + impl)
│   │   ├── repository/      # Spring Data JPA с кастомными JPQL
│   │   ├── model/           # Category, Expense entities
│   │   ├── dto/             # Request/response records
│   │   ├── mapper/          # MapStruct entity ↔ DTO
│   │   ├── exception/       # Global handler + кастомные исключения
│   │   └── util/            # XLSX export utility
│   └── src/main/resources/
│       ├── db/migration/    # Flyway миграции (V1–V6)
│       ├── application-dev.yml
│       └── application-prod.yml
│
├── spendy-frontend/         # Vue 3 SPA
│   ├── src/
│   │   ├── app/             # Корневой компонент, router, error handler
│   │   ├── features/
│   │   │   ├── auth/        # Форма логина, composable, Pinia store
│   │   │   ├── tracking/    # Сетка категорий, модалки расходов, история
│   │   │   └── statistics/  # График, таблица, выбор периода, экспорт
│   │   └── shared/
│   │       ├── api/         # Axios client + модули API по доменам
│   │       ├── ui/          # BaseButton, BaseModal
│   │       └── styles/      # CSS переменные, глобальные сбросы
│   ├── nginx.conf           # Nginx конфиг для production
│   └── Dockerfile
│
├── docker-compose.yml       # Backend + frontend сервисы
├── deploy.sh                # rsync → Docker Compose на удалённом сервере
├── ai/                      # Документация для проектирования и контекст для AI
└── docs/                    # Дополнительная документация
```

### Дизайн API

Многослойная архитектура бэкенда:

```
Controller (тонкий) → Service (интерфейс + impl, @Transactional) → Repository (Spring Data JPA)
                                                                       │
                                                                  PostgreSQL (Flyway миграции)
```

Все эндпоинты, кроме `/api/auth/login`, требуют заголовок `Authorization: Bearer <jwt>`.

### API Endpoints

**Аутентификация**
| Метод | Путь | Описание |
|---|---|---|
| POST | `/api/auth/login` | Вход, получение JWT |

**Категории**
| Метод | Путь | Описание |
|---|---|---|
| GET | `/api/categories` | Список всех категорий (сортировка по частоте за неделю) |
| POST | `/api/categories` | Создать категорию |
| PUT | `/api/categories/{id}` | Переименовать категорию |
| DELETE | `/api/categories/{id}` | Удалить категорию (каскадно удаляет расходы) |

**Расходы**
| Метод | Путь | Описание |
|---|---|---|
| GET | `/api/expenses?categoryId=` | Список расходов (пагинация, фильтрация) |
| POST | `/api/expenses` | Добавить расход |
| PUT | `/api/expenses/{id}` | Редактировать расход |
| DELETE | `/api/expenses/{id}` | Удалить расход |

**Статистика**
| Метод | Путь | Описание |
|---|---|---|
| GET | `/api/statistics?period=day\|week\|month\|year\|custom&from=&to=` | Агрегированные данные |
| GET | `/api/statistics/export?format=xlsx` | Выгрузить XLSX |

### База данных

```sql
categories (
    id          UUID PRIMARY KEY,
    name        VARCHAR NOT NULL UNIQUE,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    version     BIGINT NOT NULL DEFAULT 0
);

expenses (
    id          UUID PRIMARY KEY,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    amount      NUMERIC(14,2) NOT NULL,
    description VARCHAR(1000),
    created_by  VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    version     BIGINT NOT NULL DEFAULT 0
);
```

Оптимистичная блокировка через `@Version`. Индекс на `(category_id, created_at)`.

Аутентификация **in-memory** — таблиц `users` и `refresh_tokens` нет. Пользователи задаются в YAML-конфиге.

---

## Быстрый старт

### Требования

- Java 25 (рекомендуется Temurin)
- Node.js 25+
- PostgreSQL 17 (локально или удалённо)

### Backend

```bash
cd spendy-backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# API: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui/index.html
```

### Frontend

```bash
cd spendy-frontend
npm install
npm run dev
# http://localhost:5173
```

Vite автоматически проксирует `/api/*` → `http://localhost:8080/api/`.

### Тестирование

```bash
cd spendy-backend && mvn test
cd spendy-frontend && npm test
```

---

## Docker-деплой

### Схема production

```
Nginx Proxy Manager (SSL)
  │
  ├── https://ваш-домен/  →  spendy-frontend (nginx, порт 80)
  │                              └── /api/  →  spendy-backend:8080
  │
  └── PostgreSQL (на хосте или в контейнере)
```

### Деплой

```bash
./deploy.sh
```

Скрипт синхронизирует файлы через `rsync` по SSH-алиасу `sandra` и выполняет `docker compose up --build -d`.

### Управление контейнерами

```bash
ssh sandra

docker logs spendy-backend -f
docker logs spendy-frontend -f

docker compose -f /opt/spendy/docker-compose.yml restart
docker compose -f /opt/spendy/docker-compose.yml down
docker compose -f /opt/spendy/docker-compose.yml up --build -d
```

### Архитектурные решения

- **Flyway через `@PostConstruct`** — Spring Boot 4 убрал авто-конфигурацию Flyway для non-web контекстов; настраивается программно.
- **Нет регистрации** — все пользователи статичны, задаются в `application-{profile}.yml`. Подходит для семьи / небольшой группы.
- **Нет refresh-токенов** — при истечении JWT пользователь перенаправляется на логин.
- **`docker compose down --rmi all` в deploy** — гарантирует отсутствие устаревших образов после пересборки.
- **MapStruct вместо ручного маппинга** — уменьшает шаблонный код для преобразования entity ↔ DTO.

---

## Стиль кода

### Backend

- Lombok: `@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`, `@Slf4j`
- Java-код на английском, UI-строки на русском
- Слои: Controller → Service (interface + impl) → Repository
- DTO records на границах API, entity для JPA
- `@Transactional` на методах сервиса
- Flyway + `ddl-auto: validate` (dev) / `none` (prod)
- RFC 7807 `ProblemDetail` для ошибок через `@RestControllerAdvice`

### Frontend

- Vue 3 Composition API с `<script setup lang="ts">`
- Feature-based структура папок (auth / tracking / statistics)
- Pinia store на каждый feature
- Axios instance с JWT-интерцептором и retry-логикой
- Переиспользуемые `BaseButton` и `BaseModal`
- Mobile-first CSS с кастомными свойствами (`variables.css`)

---

## Лицензия

Проект распространяется **без лицензии** — все права защищены.