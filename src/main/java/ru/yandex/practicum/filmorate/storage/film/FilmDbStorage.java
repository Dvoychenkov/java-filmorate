package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.base.BaseCRUDRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    private static final String SQL_DELETE_FILM = "DELETE FROM films WHERE id = ?";
    private static final String SQL_DELETE_LIKES_BY_FILM_ID = "DELETE FROM films_users_likes WHERE film_id = ?";



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

        if (id == null) return null;
        film.setId(id);

        insertGenres(id, film.getGenres());

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
        insertGenres(film.getId(), film.getGenres());

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
    public void removeFilm(Long id) {
        update(SQL_DELETE_GENRES_BY_FILM_ID, id);
        update(SQL_DELETE_LIKES_BY_FILM_ID, id);
        update(SQL_DELETE_FILM, id);
        log.info("Фильм с ID {} удалён из БД", id);
    }

    private void insertGenres(Long filmId, List<Genre> genres) {
        if (genres == null) return;
        genres.stream()
                .filter(g -> g.getId() != null)
                .distinct()
                .forEach(genre -> update(SQL_INSERT_GENRE, filmId, genre.getId()));
    }

    private void deleteGenres(Long filmId) {
        update(SQL_DELETE_GENRES_BY_FILM_ID, filmId);
    }
}
