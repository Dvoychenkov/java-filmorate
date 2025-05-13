package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.base.BaseCRUDRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Primary
public class DirectorDbStorage extends BaseCRUDRepository<Director> implements DirectorStorage {

    private static final String SQL_SELECT_ALL = "SELECT * FROM directors";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM directors WHERE id = ?";
    private static final String SQL_INSERT = """
            INSERT INTO directors (name)
            VALUES (?)
            """;
    private static final String SQL_DELETE_BY_ID = "DELETE FROM directors WHERE id = ?";
    private static final String SQL_UPDATE = """
            UPDATE directors
            SET name = ?
            WHERE id = ?
            """;
    private static final String SQL_SELECT_BY_FILM_ID = """
            SELECT d.* FROM directors d
            JOIN films_directors fd ON d.id = fd.director_id
            WHERE fd.film_id = ?
            """;
    private static final String SQL_DELETE_FILMS_OF_DIRECTOR = """
            DELETE FROM films_directors
            WHERE director_id = ?
            """;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Director> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public Collection<Director> getAll() {
        return queryMany(SQL_SELECT_ALL);
    }

    @Override
    public Optional<Director> getById(Long id) {
        Optional<Director> optDirector = queryOne(SQL_SELECT_BY_ID, id);
        optDirector.ifPresentOrElse(
                (director) -> log.info("Режиссер с ID {} в БД найден: {}", id, director),
                    () -> log.info("Режиссер с ID {} в БД не найден", id)
        );
        return optDirector;
    }

    @Override
    public Director add(Director director) {
        Long id = insertAndReturnId(SQL_INSERT,
                director.getName());

        if (id == null) return null;
        director.setId(id);

        log.info("Режиссер добавлен в БД: {}", director);
        return director;
    }

    @Override
    public void delete(Long id) {
        delete(SQL_DELETE_FILMS_OF_DIRECTOR, id);
        int result = delete(SQL_DELETE_BY_ID, id);
        if (result > 0) {
            log.info("Режиссер удален");
        } else {
            throw new NotFoundException("Режиссер не удалён");
        }
    }

    @Override
    public Director update(Director director) {
        update(SQL_UPDATE,
                director.getName(),
                director.getId()
        );

        log.info("Режиссер обновлён в БД: {}", director);
        return director;
    }

    @Override
    public Collection<Director> getByFilmId(Long filmId) {
        Collection<Director> directors = queryMany(SQL_SELECT_BY_FILM_ID, filmId);
        log.info("Получено {} режиссеров из БД по id фильма {}", directors.size(), filmId);
        return directors;
    }
}
