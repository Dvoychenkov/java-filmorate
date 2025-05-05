package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.filmorate.util.TestHelper.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class, GenreRowMapper.class,
        FilmDbStorage.class, FilmRowMapper.class,
        MpaRatingDbStorage.class, MpaRatingRowMapper.class})
class GenreDbStorageTest {
    private final MpaRatingDbStorage mpaStorage;
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreStorage;

    @Test
    void shouldReturnAllGenres() {
        int genresSize = 6;
        Collection<Genre> genres = genreStorage.getAll();
        assertThat(genres).isNotNull();
        assertThat(genres).isNotEmpty();
        assertThat(genres).hasSize(genresSize);
    }

    @Test
    void shouldReturnGenreById() {
        Genre genre1 = new Genre(1L, "COMEDY", "Комедия", "");

        Optional<Genre> optGenre = genreStorage.getById(genre1.getId());
        assertThat(optGenre)
                .isPresent()
                .contains(genre1);

        Genre genre6 = new Genre(6L, "ACTION", "Боевик", "");

        optGenre = genreStorage.getById(genre6.getId());
        assertThat(optGenre)
                .isPresent()
                .contains(genre6);
    }

    @Test
    void shouldReturnGenresByFilmId() {
        MpaRating mpaCreate = getRequired(mpaStorage.getById(1L), NOT_FOUND_MPA);
        List<Genre> genresCreate = List.of(
                getRequired(genreStorage.getById(1L), NOT_FOUND_GENRE),
                getRequired(genreStorage.getById(6L), NOT_FOUND_GENRE)
        );
        Film filmToCreate = generateFilm(mpaCreate, genresCreate);
        Film createdFilm = filmStorage.add(filmToCreate);

        Collection<Genre> genresOfFilm = genreStorage.getByFilmId(createdFilm.getId());
        assertEquals(genresOfFilm.size(), genresCreate.size());
        assertEquals(genresOfFilm, genresCreate);
    }
}