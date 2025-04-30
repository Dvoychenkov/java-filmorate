package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.base.BaseCRUDRepository;

import java.util.Collection;

@Slf4j
@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseCRUDRepository<Film> implements FilmStorage {
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
                film.getMpaRating().getId()
        );
        film.setId(id);
        log.info("Фильм добавлен в БД: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        // TODO до-определиться, нужна ли тут проверка + по аналогии для юзеров
        getById(film.getId());

        update(SQL_UPDATE,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRating().getId(),
                film.getId()
        );
        log.info("Фильм обновлён в БД: {}", film);
        return film;
    }

    // TODO убрать выброс исключения, возвращать Optional
    @Override
    public Film getById(Long id) {
        return queryOne(SQL_SELECT_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
    }
}
