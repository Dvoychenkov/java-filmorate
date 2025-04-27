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
