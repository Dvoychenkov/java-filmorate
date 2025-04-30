package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.base.BaseReadRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
@Slf4j
public class GenreDbStorage extends BaseReadRepository<Genre> implements GenreStorage {
    private static final String SQL_SELECT_ALL = "SELECT * FROM genres";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM genres WHERE id = ?";
    private static final String SQL_SELECT_BY_FILM_ID = """
            SELECT g.* FROM genres g
            JOIN films_genres fg ON g.id = fg.genre_id
            WHERE fg.film_id = ?
            """;

    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper genreRowMapper) {
        super(jdbcTemplate, genreRowMapper);
    }

    @Override
    public Collection<Genre> getAll() {
        Collection<Genre> genres = queryMany(SQL_SELECT_ALL);
        log.info("Получено {} жанров из БД", genres.size());
        return genres;
    }

    @Override
    public Optional<Genre> getById(Long id) {
        Optional<Genre> optGenre = queryOne(SQL_SELECT_BY_ID, id);
        if (optGenre.isEmpty()) {
            log.info("Жанр с ID {} в БД не найден", id);
        } else {
            log.info("Жанр с ID {} в БД найден: {}", id, optGenre.get());
        }
        return optGenre;
    }

    @Override
    public Collection<Genre> getByFilmId(Long filmId) {
        Collection<Genre> genres = queryMany(SQL_SELECT_BY_FILM_ID, filmId);
        log.info("Получено {} жанров из БД по id фильма {}", genres.size(), filmId);
        return genres;
    }
}
