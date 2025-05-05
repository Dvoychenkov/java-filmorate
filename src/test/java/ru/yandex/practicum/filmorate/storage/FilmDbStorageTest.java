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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserRowMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.util.TestHelper.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class,
        GenreDbStorage.class, GenreRowMapper.class,
        MpaRatingDbStorage.class, MpaRatingRowMapper.class,
        UserDbStorage.class, UserRowMapper.class})
class FilmDbStorageTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final MpaRatingDbStorage mpaStorage;

    @Test
    void shouldReturnAllFilms() {
        Collection<Film> initFilms = filmStorage.getAll();
        assertThat(initFilms).isNotNull();
        assertThat(initFilms).isEmpty();

        int addedCnt = 10;
        MpaRating mpa = getRequired(mpaStorage.getById(1L), NOT_FOUND_MPA);
        for (long i = 1; i <= addedCnt; i++) {
            filmStorage.add(generateFilm(mpa));
        }

        Collection<Film> allFilms = filmStorage.getAll();
        assertThat(allFilms).isNotNull();
        assertThat(allFilms).isNotEmpty();
        assertThat(allFilms).hasSize(addedCnt);
    }

    @Test
    void shouldAddAndGetFilm() {
        MpaRating mpaCreate = getRequired(mpaStorage.getById(1L), NOT_FOUND_MPA);
        List<Genre> genresCreate = List.of(
                getRequired(genreStorage.getById(1L), NOT_FOUND_GENRE),
                getRequired(genreStorage.getById(6L), NOT_FOUND_GENRE)
        );
        Film filmToCreate = generateFilm(mpaCreate, genresCreate);

        Film createdFilm = filmStorage.add(filmToCreate);
        Optional<Film> createdFilmFromDB = filmStorage.getById(createdFilm.getId());

        assertThat(createdFilmFromDB)
                .isPresent()
                .contains(createdFilm);
    }

    @Test
    void shouldUpdateFilm() {
        MpaRating mpaCreate = getRequired(mpaStorage.getById(1L), NOT_FOUND_MPA);
        List<Genre> genresCreate = List.of(
                getRequired(genreStorage.getById(1L), NOT_FOUND_GENRE),
                getRequired(genreStorage.getById(6L), NOT_FOUND_GENRE)
        );

        Film filmToCreate = generateFilm(mpaCreate, genresCreate);
        Film createdFilm = filmStorage.add(filmToCreate);

        MpaRating mpaUpdate = getRequired(mpaStorage.getById(5L), NOT_FOUND_MPA);
        List<Genre> genresUpdate = List.of(
                getRequired(genreStorage.getById(2L), NOT_FOUND_GENRE),
                getRequired(genreStorage.getById(5L), NOT_FOUND_GENRE)
        );
        Film filmToUpdate = generateFilm(mpaUpdate, genresUpdate);
        filmToUpdate.setId(createdFilm.getId());
        Film updatedFilm = filmStorage.update(filmToUpdate);

        Optional<Film> updatedFilmFromDB = filmStorage.getById(updatedFilm.getId());
        assertThat(updatedFilmFromDB)
                .isPresent()
                .contains(filmToUpdate);
    }

    @Test
    void shouldAddAndRemoveLike() {
        User userToCreate = generateUser();
        User createdUser = userStorage.add(userToCreate);
        User createdUserFromDB = getRequired(userStorage.getById(createdUser.getId()), NOT_FOUND_USER);

        MpaRating mpaCreate = getRequired(mpaStorage.getById(1L), NOT_FOUND_MPA);
        Film filmToCreate = generateFilm(mpaCreate);
        Film createdFilm = filmStorage.add(filmToCreate);

        // Пробуем снять лайк, который не ставили
        assertFalse(filmStorage.removeLike(createdFilm.getId(), createdUserFromDB.getId()));
        // Ставим лайк
        assertTrue(filmStorage.addLike(createdFilm.getId(), createdUserFromDB.getId()));
        // Ставим лайк повторно
        assertFalse(filmStorage.addLike(createdFilm.getId(), createdUserFromDB.getId()));
        // Убираем лайк
        assertTrue(filmStorage.removeLike(createdFilm.getId(), createdUserFromDB.getId()));
        // Убираем лайк повторно
        assertFalse(filmStorage.removeLike(createdFilm.getId(), createdUserFromDB.getId()));
    }

    @Test
    void shouldReturnTopFilmsByLikes() {
        // Добавляем 10 фильмов
        MpaRating mpa = getRequired(mpaStorage.getById(1L), NOT_FOUND_MPA);
        List<Film> films = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            Film film = filmStorage.add(generateFilm(mpa));
            films.add(film);

            // Ставим лайки
            for (int j = 1; j <= i * 10; j++) {
                User user = userStorage.add(generateUser());
                filmStorage.addLike(film.getId(), user.getId());
            }
        }

        // Получаем топ-5
        int topCnt = 5;
        Collection<Film> top = filmStorage.getTopFilmsByLikes(topCnt);
        assertThat(top.size()).isEqualTo(topCnt);

        // Ожидаем 5 фильмов с конца, получаем их id
        List<Long> expectedFilmsIds = films.stream()
                .skip(Math.max(0, films.size() - 5))
                .map(Film::getId)
                .toList()
                .reversed();

        List<Long> actualIds = top.stream()
                .map(Film::getId)
                .toList();
        assertThat(actualIds).isEqualTo(expectedFilmsIds);
    }
}