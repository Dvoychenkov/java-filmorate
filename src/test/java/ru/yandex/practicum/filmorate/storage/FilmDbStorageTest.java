package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorRowMapper;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserRowMapper;

import java.time.LocalDate;
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
        UserDbStorage.class, UserRowMapper.class,
        DirectorDbStorage.class, DirectorRowMapper.class
})
class FilmDbStorageTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final MpaRatingDbStorage mpaStorage;
    private final DirectorStorage directorStorage;

    @Test
    void shouldReturnAllFilms() {
        Collection<Film> initFilms = filmStorage.getAll();
        assertThat(initFilms).isNotNull();
        assertThat(initFilms).isEmpty();

        Director directorToCreate = generateDirector();
        Director createdDirector = directorStorage.add(directorToCreate);
        List<Director> directors = new ArrayList<>();
        directors.add(createdDirector);
        int addedCnt = 10;
        MpaRating mpa = getRequired(mpaStorage.getById(1L), NOT_FOUND_MPA);
        for (long i = 1; i <= addedCnt; i++) {
            filmStorage.add(generateFilm(mpa, directors));
        }

        Collection<Film> allFilms = filmStorage.getAll();
        assertThat(allFilms).isNotNull();
        assertThat(allFilms).isNotEmpty();
        assertThat(allFilms).hasSize(addedCnt);
    }

    @Test
    void shouldAddAndGetFilm() {
        Director directorToCreate = generateDirector();
        Director createdDirector = directorStorage.add(directorToCreate);
        List<Director> directors = new ArrayList<>();
        directors.add(createdDirector);
        MpaRating mpaCreate = getRequired(mpaStorage.getById(1L), NOT_FOUND_MPA);
        List<Genre> genresCreate = List.of(
                getRequired(genreStorage.getById(1L), NOT_FOUND_GENRE),
                getRequired(genreStorage.getById(6L), NOT_FOUND_GENRE)
        );
        Film filmToCreate = generateFilm(mpaCreate, directors, genresCreate);

        Film createdFilm = filmStorage.add(filmToCreate);
        Optional<Film> createdFilmFromDB = filmStorage.getById(createdFilm.getId());

        assertThat(createdFilmFromDB)
                .isPresent()
                .contains(createdFilm);
    }

    @Test
    void shouldUpdateFilm() {
        Director directorToCreate = generateDirector();
        Director createdDirector = directorStorage.add(directorToCreate);
        List<Director> directors = new ArrayList<>();
        directors.add(createdDirector);
        MpaRating mpaCreate = getRequired(mpaStorage.getById(1L), NOT_FOUND_MPA);
        List<Genre> genresCreate = List.of(
                getRequired(genreStorage.getById(1L), NOT_FOUND_GENRE),
                getRequired(genreStorage.getById(6L), NOT_FOUND_GENRE)
        );

        Film filmToCreate = generateFilm(mpaCreate, directors, genresCreate);
        Film createdFilm = filmStorage.add(filmToCreate);

        MpaRating mpaUpdate = getRequired(mpaStorage.getById(5L), NOT_FOUND_MPA);
        List<Genre> genresUpdate = List.of(
                getRequired(genreStorage.getById(2L), NOT_FOUND_GENRE),
                getRequired(genreStorage.getById(5L), NOT_FOUND_GENRE)
        );
        Director newDirectorToCreate = generateDirector();
        Director createdNewDirector = directorStorage.add(newDirectorToCreate);
        List<Director> newDirectors = new ArrayList<>();
        directors.add(createdNewDirector);
        Film filmToUpdate = generateFilm(mpaUpdate, newDirectors, genresUpdate);
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

        Director directorToCreate = generateDirector();
        Director createdDirector = directorStorage.add(directorToCreate);
        List<Director> directors = new ArrayList<>();
        directors.add(createdDirector);
        MpaRating mpaCreate = getRequired(mpaStorage.getById(1L), NOT_FOUND_MPA);
        Film filmToCreate = generateFilm(mpaCreate, directors);
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
        Director directorToCreate = generateDirector();
        Director createdDirector = directorStorage.add(directorToCreate);
        List<Director> directors = new ArrayList<>();
        directors.add(createdDirector);
        MpaRating mpa = getRequired(mpaStorage.getById(1L), NOT_FOUND_MPA);
        List<Film> films = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            Film film = filmStorage.add(generateFilm(mpa, directors));
            films.add(film);

            // Ставим лайки
            for (int j = 1; j <= i * 10; j++) {
                User user = userStorage.add(generateUser());
                filmStorage.addLike(film.getId(), user.getId());
            }
        }

        // Получаем топ-5
        int topCnt = 5;
        Collection<Film> top = filmStorage.getTopFilmsByLikes(topCnt, null, null);
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

    @Test
    void shouldReturnDirectorFilmsSortedByYears() {
        // Добавляем 10 фильмов
        Director directorToCreate1 = generateDirector();
        Director createdDirector1 = directorStorage.add(directorToCreate1);
        List<Director> directors1 = new ArrayList<>();
        directors1.add(createdDirector1);

        Director directorToCreate2 = generateDirector();
        Director createdDirector2 = directorStorage.add(directorToCreate2);
        List<Director> directors2 = new ArrayList<>();
        directors2.add(createdDirector2);

        MpaRating mpa = getRequired(mpaStorage.getById(1L), NOT_FOUND_MPA);
        List<Film> films = new ArrayList<>();
        int year1 = 2008;
        for (long i = 0; i < 10; i++) {
            Film filmToAdd = generateFilm(mpa, directors1);
            filmToAdd.setReleaseDate(LocalDate.of(year1, 1, 1));

            Film film = filmStorage.add(filmToAdd);
            films.add(film);
            year1++;
        }

        int year2 = 2010;
        MpaRating mpa2 = getRequired(mpaStorage.getById(2L), NOT_FOUND_MPA);
        List<Film> films2 = new ArrayList<>();
        for (long i = 0; i < 9; i++) {
            Film filmToAdd = generateFilm(mpa2, directors2);
            filmToAdd.setReleaseDate(LocalDate.of(year2, 1, 1));

            Film film = filmStorage.add(filmToAdd);
            films2.add(film);
            year2++;
        }

        // Получаем топ-5
        Collection<Film> directorFilmsSortedByYears = filmStorage.getDirectorFilmsSortedByYears(createdDirector1.getId());
        assertThat(directorFilmsSortedByYears.size()).isEqualTo(10);

        Collection<Film> director2FilmsSortedByYears = filmStorage.getDirectorFilmsSortedByYears(createdDirector2.getId());
        assertThat(director2FilmsSortedByYears.size()).isEqualTo(9);

        int year = directorFilmsSortedByYears.stream().findFirst().orElseThrow().getReleaseDate().getYear();
        assertThat(year).isEqualTo(2008);
        year = directorFilmsSortedByYears.stream().skip(9).findFirst().orElseThrow().getReleaseDate().getYear();
        assertThat(year).isEqualTo(2017);

        year = director2FilmsSortedByYears.stream().findFirst().orElseThrow().getReleaseDate().getYear();
        assertThat(year).isEqualTo(2010);
        year = director2FilmsSortedByYears.stream().skip(8).findFirst().orElseThrow().getReleaseDate().getYear();
        assertThat(year).isEqualTo(2018);
    }

    @Test
    void shouldReturnDirectorFilmsSortedByLikes() {
        // Добавляем 10 фильмов
        Director directorToCreate1 = generateDirector();
        Director createdDirector1 = directorStorage.add(directorToCreate1);
        List<Director> directors = new ArrayList<>();
        directors.add(createdDirector1);

        List<User> users = new ArrayList<>();
        for (long i = 0; i < 20; i++) {
            User user = generateUser();
            User createdUser = userStorage.add(user);
            users.add(createdUser);
        }

        List<Film> films = new ArrayList<>();
        for (long i = 1; i <= 2; i++) {
            MpaRating mpa = getRequired(mpaStorage.getById(i), NOT_FOUND_MPA);
            Film filmToAdd = generateFilm(mpa, directors);
            Film film = filmStorage.add(filmToAdd);
            films.add(film);
        }

        for (int i = 0; i < 5; i++) {
            filmStorage.addLike(films.get(0).getId(), users.get(i).getId());
        }
        for (int i = 0; i < 8; i++) {
            int j = i + 5;
            filmStorage.addLike(films.get(1).getId(), users.get(j).getId());
        }

        // Получаем топ-5
        Collection<Film> directorFilmsSortedByYears = filmStorage.getDirectorFilmsSortedByLikes(createdDirector1.getId());
        assertThat(directorFilmsSortedByYears.size()).isEqualTo(2);

        assertThat(directorFilmsSortedByYears.stream().findFirst().orElseThrow().getMpa().getId()).isEqualTo(2);
        assertThat(directorFilmsSortedByYears.stream().skip(1).findFirst().orElseThrow().getMpa().getId()).isEqualTo(1);
    }

    @Test
    void shouldRemoveFilm() {
        MpaRating mpa = getRequired(mpaStorage.getById(1L), NOT_FOUND_MPA);
        Director directorToCreate1 = generateDirector();
        Director createdDirector1 = directorStorage.add(directorToCreate1);
        List<Director> directors = new ArrayList<>();
        Film filmToCreate = generateFilm(mpa, directors);
        Film createdFilm = filmStorage.add(filmToCreate);
        Long filmId = createdFilm.getId();

        filmStorage.removeFilm(filmId);
        Optional<Film> filmFromDb = filmStorage.getById(filmId);
        assertThat(filmFromDb).isNotPresent();
    }
}