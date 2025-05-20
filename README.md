# java-filmorate

# Архитектура БД

## ER-диаграмма

![ER-диаграмма](/assets/images/sql_diagram.svg)

---

## Описание таблиц

### users
Хранение информации о пользователях:
- `id` — первичный ключ пользователя (int).
- `email` — адрес электронной почты пользователя (varchar, not null).
- `login` — логин пользователя (varchar, not null).
- `name` — имя пользователя (varchar, not null).
- `birthday` — дата рождения пользователя (date, not null).

### friendship_status
Справочник статусов дружбы:
- `id` — первичный ключ статуса (int).
- `code` — код статуса (varchar, not null).
- `name` — наименование статуса (varchar, not null).
- `description` — описание статуса (varchar, nullable).

### users_friendship
Связь между пользователями и их друзьями:
- `id` — первичный ключ записи дружбы (int).
- `user_id` — ID пользователя, отправившего запрос (int, FK users).
- `friend_id` — ID пользователя, которому отправлен запрос (int, FK users).
- `status_id` — ID статуса дружбы (int, FK friendship_status).

### films
Хранение фильмов:
- `id` — первичный ключ фильма (int).
- `name` — название фильма (varchar, not null).
- `description` — описание фильма (varchar, not null).
- `release_date` — дата выхода фильма (date, not null).
- `duration` — продолжительность фильма в минутах (int, not null).
- `mpa_rating_id` — ID возрастного рейтинга фильма (int, FK mpa_ratings).

### films_users_likes
Лайки фильмов пользователями:
- `id` — первичный ключ записи лайка (int).
- `film_id` — ID фильма (int, FK films).
- `user_id` — ID пользователя, поставившего лайк (int, FK users).

### genres
Справочник жанров фильмов:
- `id` — первичный ключ жанра (int).
- `code` — код жанра (varchar, not null).
- `name` — наименование жанра (varchar, not null).
- `description` — описание жанра (varchar, nullable).

### films_genres
Связь фильмов и жанров:
- `id` — первичный ключ записи связи (int).
- `film_id` — ID фильма (int, FK films).
- `genre_id` — ID жанра (int, FK genres).

### mpa_ratings
Справочник возрастных рейтингов фильмов:
- `id` — первичный ключ рейтинга (int).
- `code` — код рейтинга (varchar, not null).
- `name` — наименование рейтинга (varchar, not null).
- `description` — описание рейтинга (varchar, nullable).

---

## Примеры SQL-запросов


### Получить все фильмы

```sql
SELECT * FROM films;
```

### Получить всех пользователей

```sql
SELECT * FROM users;
```

### Получить топ N популярных фильмов по количеству лайков

```sql
SELECT f.*
FROM films f
LEFT JOIN films_users_likes ful ON f.id = ful.film_id
GROUP BY f.id
ORDER BY COUNT(ful.user_id) DESC
LIMIT :N;
```

### Получить список друзей пользователя

```sql
SELECT u.*
FROM users u
JOIN users_friendship uf ON u.id = uf.friend_id
WHERE uf.user_id = :userId
AND uf.status_id = (SELECT id FROM friendship_status WHERE code = 'CONFIRMED');
```

### Получить все лайки пользователя

```sql
SELECT f.*
FROM films f
JOIN films_users_likes ful ON f.id = ful.film_id
WHERE ful.user_id = :userId;

```

### Получить жанры определённого фильма

```sql
SELECT g.*
FROM genres g
JOIN films_genres fg ON g.id = fg.genre_id
WHERE fg.film_id = :filmId;
```

### Получить рейтинг MPA определённого фильма

```sql
SELECT r.*
FROM mpa_ratings r
JOIN films f ON r.id = f.mpa_rating_id
WHERE f.id = :filmId;
```

---

##  Реализованные задачи в рамках группового спринта

### 1. Удаление фильмов и пользователей
Добавлена поддержка удаления по идентификатору:
-   `DELETE /users/{userId}` — удаление пользователя
-   `DELETE /films/{filmId}` — удаление фильма

---

### 2. Отзывы к фильмам (`/reviews`)
Реализована система отзывов с лайками и рейтингом полезности:
-   `POST /reviews` — создание
-   `PUT /reviews` — редактирование
-   `DELETE /reviews/{id}` — удаление
-   `GET /reviews/{id}` — получение по ID
-   `GET /reviews?filmId=&count=` — список по фильму
-   `PUT/DELETE /reviews/{id}/like/{userId}` — лайки
-   `PUT/DELETE /reviews/{id}/dislike/{userId}` — дизлайки

У отзыва есть:
-   текст (`content`)
-   тип (`isPositive`)
-   рейтинг (`useful`, увеличивается/уменьшается при голосах)

---

### 3. Режиссёры и сортировка фильмов (`/films/director`)
Добавлена поддержка работы с режиссёрами:
-   `GET /directors` — получение всех
-   `GET /directors/{id}` — получение по ID
-   `POST /directors` — создание
-   `PUT /directors` — редактирование
-   `DELETE /directors/{id}` — удаление

Фильмы можно сортировать:
-   по лайкам: `GET /films/director/{id}?sortBy=likes`
-   по годам: `GET /films/director/{id}?sortBy=year`

---

### 4. Лента событий (`/users/{id}/feed`)
Добавлена лента активности пользователя:
-   поддержка событий `LIKE`, `REVIEW`, `FRIEND`
-   операции `ADD`, `REMOVE`, `UPDATE` (обновление только для отзывов)
-   `GET /users/{id}/feed` возвращает хронологический список действий

---

### 5. Рекомендации фильмов (`/users/{id}/recommendations`)
Реализован простой алгоритм рекомендаций:
1. находит пользователя с похожими вкусами
2. рекомендует фильмы, которые он лайкал, а целевой пользователь — нет
-   `GET /users/{id}/recommendations`

---

### 6. Общие фильмы (`/films/common`)
Добавлен эндпоинт:
-   `GET /films/common?userId=&friendId=`
-   возвращает фильмы, лайкнутые обоими пользователями
-   отсортировано по количеству лайков (популярности)

---

### 7. Популярные фильмы по фильтрам (`/films/popular`)
-   `GET /films/popular?count=&genreId=&year=`
-   поддержка фильтрации по жанру и году
-   отсортировано по количеству лайков

---

### 8. Поиск фильмов (`/films/search`)
Реализован поиск по подстроке:
-   `GET /films/search?query=...&by=title,director`
-   ищет по названию фильма и/или имени режиссёра
-   сортировка по популярности

---

### Произведённый рефакторинг и улучшения
-   Все ID валидируются через кастомную аннотацию `@IdValid("userId" / "filmId" / ...)`
-   Используются `@NotNull`, `@NotBlank`, `@Size`, `@Min` для параметров
-   Поддержка `enum`-значений через Spring `Converter` (`SortOption`, `SearchByField`)
-   В целом вся валидация вынесена в контроллеры
-   Выброс исключений для create операций перенесён в storage для каждого функционала