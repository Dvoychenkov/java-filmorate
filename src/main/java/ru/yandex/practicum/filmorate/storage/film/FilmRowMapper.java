package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final MpaRatingStorage mpaRatingStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long filmId = rs.getLong("id");

        Film film = new Film();
        film.setId(filmId);
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        // Получение жанров фильма
        film.setGenres(genreStorage.getByFilmId(filmId).stream().toList());

        // Получение рейтинга фильма
        Long mpaId = rs.getLong("mpa_rating_id");
        MpaRating rating = mpaRatingStorage.getById(mpaId)
                .orElseThrow(() -> new NotFoundException("MPA рейтинг не найден по id: " + mpaId));
        film.setMpa(rating);

        Long directorId = rs.getLong("director_id");
        Director director = directorStorage.getById(directorId)
                .orElseThrow(() -> new NotFoundException("Режиссер не найден по id: " + directorId));
        film.setDirector(director);

        return film;
    }
}
