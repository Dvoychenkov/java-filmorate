package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.base.BaseCRUDRepository;

import java.util.*;

@Slf4j
@Repository
@Primary
public class FilmDbStorage extends BaseCRUDRepository<Film> implements FilmStorage {
    // Обработка информации о фильмах
    private static final String SQL_SELECT_ALL = "SELECT * FROM films";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM films WHERE id = ?";
    private static final String SQL_INSERT = """
            INSERT INTO films (name, description, release_date, duration, mpa_rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String SQL_UPDATE = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?
            WHERE id = ?
            """;

    // Обработка информации о жанрах
    private static final String SQL_INSERT_GENRE = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String SQL_DELETE_GENRES_BY_FILM_ID = "DELETE FROM films_genres WHERE film_id = ?";

    // Обработка информации о лайках
    private static final String SQL_INSERT_LIKE = """
            INSERT INTO films_users_likes (film_id, user_id)
            SELECT ?, ?
            WHERE NOT EXISTS (
                SELECT 1 FROM films_users_likes WHERE film_id = ? AND user_id = ?
            )
            """;
    private static final String SQL_DELETE_LIKE = "DELETE FROM films_users_likes WHERE film_id = ? AND user_id = ?";
    private static final String SQL_SELECT_TOP_FILMS = """
                SELECT f.*
                FROM films f
                LEFT JOIN films_users_likes l ON f.id = l.film_id
                GROUP BY f.id
                ORDER BY COUNT(l.user_id) DESC
                LIMIT ?
            """;
    private static final String SQL_DELETE_FILM = "DELETE FROM films WHERE id = ?";
    private static final String SQL_DELETE_LIKES_BY_FILM_ID = "DELETE FROM films_users_likes WHERE film_id = ?";

    // Обработка информации о рекомендациях
    /**
     * Запрос для получения рекомендованных фильмов для указанного юзера.
     * Алгоритм:
     * 1. Выбираем топ-юзера, у которого больше всего общих лайкнутых фильмов с целевым юзером;
     * 2. Получаем все фильмы, который лайкнул топ-юзер;
     * 3. Исключаем те фильмы, которые лайкнул целевой юзер.
     * Параметр:
     * userId — используется трижды: для исключения из сравнения, поиска совпадений и исключения уже просмотренного.
     */
    private static final String SQL_SELECT_RECOMMENDATIONS = """
                SELECT * FROM films
                WHERE id IN (
                    SELECT film_id FROM films_users_likes
                    WHERE user_id = (
                        SELECT user_id FROM films_users_likes
                        WHERE user_id != ?
                          AND film_id IN (
                              SELECT film_id FROM films_users_likes WHERE user_id = ?
                          )
                        GROUP BY user_id
                        ORDER BY COUNT(1) DESC
                        LIMIT 1
                    )
                )
                AND id NOT IN (
                    SELECT film_id FROM films_users_likes WHERE user_id = ?
                );
            """;


    // Обработка информации о режиссерах
    private static final String SQL_INSERT_DIRECTOR = "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)";
    private static final String SQL_DELETE_DIRECTORS_BY_FILM_ID = "DELETE FROM films_directors WHERE film_id = ?";

    // Обработка информации о фильмах режиссёра
    private static final String SQL_DIRECTOR_FILMS_YEARS = """
            SELECT f.*
            FROM films f
            JOIN films_directors fd ON (f.id = fd.film_id)
            WHERE fd.director_id = ?
            ORDER BY release_date
            """;
    private static final String SQL_DIRECTOR_FILMS_LIKES = """
                SELECT f.*
                FROM films f
                JOIN films_directors fd ON f.id = fd.film_id
                LEFT JOIN films_users_likes l ON f.id = l.film_id
                WHERE fd.director_id = ?
                GROUP BY f.id
                ORDER BY COUNT(l.user_id) DESC
            """;

    // Обработка информации о фильмах
    /**
     * Запрос для получения фильмов, которые лайкнули и userId, и friendId,
     * отсортированных по общему количеству лайков (популярности).
     */
    private static final String SQL_SELECT_COMMON_FILMS_POPULAR_BY_LIKES = """
                SELECT f.*, COUNT(ful3.film_id) AS likes_count
                FROM films f
                JOIN films_users_likes ful1 ON f.id = ful1.film_id AND ful1.user_id = ?
                JOIN films_users_likes ful2 ON f.id = ful2.film_id AND ful2.user_id = ?
                LEFT JOIN films_users_likes ful3 ON f.id = ful3.film_id
                GROUP BY f.id
                ORDER BY likes_count DESC;
            """;
    private static final String SQL_SELECT_TOP_FILMS_BY_GENRE = """
                SELECT f.*
                FROM films f
                JOIN films_genres fg ON f.id = fg.film_id
                LEFT JOIN films_users_likes l ON f.id = l.film_id
                WHERE fg.genre_id = ?
                GROUP BY f.id
                ORDER BY COUNT(l.user_id) DESC
                LIMIT ?
            """;
    private static final String SQL_SELECT_TOP_FILMS_BY_YEAR = """
                SELECT f.*
                FROM films f
                LEFT JOIN films_users_likes l ON f.id = l.film_id
                WHERE EXTRACT(YEAR FROM release_date) = ?
                GROUP BY f.id
                ORDER BY COUNT(l.user_id) DESC
                LIMIT ?
            """;
    private static final String SQL_SELECT_TOP_FILMS_BY_GENRE_AND_YEAR = """
                SELECT f.*
                FROM films f
                JOIN films_genres fg ON f.id = fg.film_id
                LEFT JOIN films_users_likes l ON f.id = l.film_id
                WHERE EXTRACT(YEAR FROM release_date) = ?
                AND fg.genre_id = ?
                GROUP BY f.id
                ORDER BY COUNT(l.user_id) DESC
                LIMIT ?
            """;

    // Поиск фильмов
    private static final String SQL_SEARCH_FILMS_SELECT = """
            SELECT
                f.id,
                f.name,
                f.description,
                f.release_date,
                f.duration,
                f.mpa_rating_id,
                m.name AS mpa_name,
                COUNT(l.user_id) AS like_count
            FROM films f
            JOIN mpa_ratings m ON f.mpa_rating_id = m.id
            LEFT JOIN films_users_likes l ON f.id = l.film_id
            LEFT JOIN films_directors fd ON f.id = fd.film_id
            LEFT JOIN directors d ON fd.director_id = d.id
            """;
    public static final String SQL_SEARCH_FILMS_GROUP_AND_ORDER = """
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, m.name
            ORDER BY like_count DESC, f.id ASC
            """;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper) {
        super(jdbcTemplate, filmRowMapper);
    }

    @Override
    public Collection<Film> getAll() {
        return queryMany(SQL_SELECT_ALL);
    }

    @Override
    public Film add(Film film) {
        Long id = insertAndReturnId(SQL_INSERT,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );

        if (id == null) throw new IllegalStateException("Не удалось сохранить данные для нового фильма");
        film.setId(id);

        insertGenres(id, film.getGenres());
        insertDirectors(id, film.getDirectors());

        log.info("Фильм добавлен в БД: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        update(SQL_UPDATE,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        deleteGenres(film.getId());
        deleteDirectors(film.getId());
        insertGenres(film.getId(), film.getGenres());
        insertDirectors(film.getId(), film.getDirectors());

        log.info("Фильм обновлён в БД: {}", film);
        return film;
    }

    @Override
    public Optional<Film> getById(Long id) {
        Optional<Film> optFilm = queryOne(SQL_SELECT_BY_ID, id);
        optFilm.ifPresentOrElse(
                (film) -> log.info("Фильм с ID {} в БД найден: {}", id, film),
                () -> log.info("Фильм с ID {} в БД не найден", id)
        );
        return optFilm;
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        int affected = update(SQL_INSERT_LIKE, filmId, userId, filmId, userId);
        return affected > 0;
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        int affected = update(SQL_DELETE_LIKE, filmId, userId);
        return affected > 0;
    }

    @Override
    public Collection<Film> getTopFilmsByLikes(int count, Integer genreId, Integer year) {
        List<Film> result;

        if (genreId != null && year != null) {
            result = queryMany(SQL_SELECT_TOP_FILMS_BY_GENRE_AND_YEAR, year, genreId, count);
        } else if (genreId != null) {
            result = queryMany(SQL_SELECT_TOP_FILMS_BY_GENRE, genreId, count);
        } else if (year != null) {
            result = queryMany(SQL_SELECT_TOP_FILMS_BY_YEAR, year, count);
        } else {
            result = queryMany(SQL_SELECT_TOP_FILMS, count);
        }
        return result;
    }

    @Override
    public Collection<Film> getDirectorFilmsSortedByYears(Long directorId) {
        return queryMany(SQL_DIRECTOR_FILMS_YEARS, directorId);
    }

    @Override
    public Collection<Film> getDirectorFilmsSortedByLikes(Long directorId) {
        return queryMany(SQL_DIRECTOR_FILMS_LIKES, directorId);
    }

    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        return queryMany(SQL_SELECT_COMMON_FILMS_POPULAR_BY_LIKES, userId, friendId);
    }

    @Override
    public void removeFilm(Long id) {
        update(SQL_DELETE_GENRES_BY_FILM_ID, id);
        update(SQL_DELETE_LIKES_BY_FILM_ID, id);
        update(SQL_DELETE_FILM, id);
        log.info("Фильм с ID {} удалён из БД", id);
    }

    @Override
    public List<Film> getFilmsRecommendations(Long userId) {
        return queryMany(SQL_SELECT_RECOMMENDATIONS, userId, userId, userId);
    }

    @Override
    public Collection<Film> searchFilms(String query, Set<String> by) {
        String searchText = "%" + query.toLowerCase() + "%";

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (by == null || by.isEmpty()) {
            conditions.add("LOWER(f.name) LIKE ?");
            conditions.add("LOWER(d.name) LIKE ?");
            params.add(searchText);
            params.add(searchText);
        } else {
            if (by.contains("title")) {
                conditions.add("LOWER(f.name) LIKE ?");
                params.add(searchText);
            }
            if (by.contains("director")) {
                conditions.add("LOWER(d.name) LIKE ?");
                params.add(searchText);
            }
        }

        String where = conditions.isEmpty() ? "" : "WHERE " + String.join(" OR ", conditions);

        String fullQuery = String.format("%s %s %s", SQL_SEARCH_FILMS_SELECT, where, SQL_SEARCH_FILMS_GROUP_AND_ORDER);
        return queryMany(fullQuery, params.toArray());
    }

    private void insertGenres(Long filmId, List<Genre> genres) {
        if (genres == null) return;
        genres.stream()
                .filter(g -> g.getId() != null)
                .distinct()
                .forEach(genre -> update(SQL_INSERT_GENRE, filmId, genre.getId()));
    }

    private void insertDirectors(Long filmId, List<Director> directors) {
        if (directors == null) return;
        directors.stream()
                .filter(g -> g.getId() != null)
                .distinct()
                .forEach(director -> update(SQL_INSERT_DIRECTOR, filmId, director.getId()));
    }

    private void deleteGenres(Long filmId) {
        update(SQL_DELETE_GENRES_BY_FILM_ID, filmId);
    }

    private void deleteDirectors(Long filmId) {
        update(SQL_DELETE_DIRECTORS_BY_FILM_ID, filmId);
    }
}