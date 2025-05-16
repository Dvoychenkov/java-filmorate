package ru.yandex.practicum.filmorate.util;

import com.github.javafaker.Faker;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.services.FeedService;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TestHelper {
    private static final Faker faker = new Faker();

    public static final String NOT_FOUND_USER = "Юзер не найден";
    public static final String NOT_FOUND_MPA = "МПА рейтинг не найден";
    public static final String NOT_FOUND_GENRE = "Жанр не найден";

    public static User generateUser() {
        return new User(
                null,
                faker.internet().emailAddress(),
                faker.name().username().replaceAll("\\s+", ""),
                faker.name().fullName(),
                generatePastDate(),
                new HashMap<>()
        );
    }

    public static Director generateDirector() {
        return new Director(
                null,
                faker.name().fullName()
        );
    }

    public static Film generateFilm(MpaRating mpaRating, List<Director> directors) {
        return generateFilm(mpaRating, directors, List.of());
    }

    public static Film generateFilm(MpaRating mpaRating, List<Director> directors, List<Genre> genres) {
        return new Film(
                null,
                faker.book().title(),
                faker.lorem().sentence(10, 10),
                generatePastDate(),
                faker.number().numberBetween(60, 180),
                mpaRating,
                new ArrayList<>(directors),
                new ArrayList<>(genres),
                new HashSet<>()
        );
    }

    private static LocalDate generatePastDate() {
        Date date = faker.date().past(365 * 50, TimeUnit.DAYS);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static <T> T getRequired(Optional<T> optional, String message) {
        String assertMessage = message == null ? "Объект не найден" : message;
        return optional.orElseThrow(() -> new AssertionError(assertMessage));
    }

    public static UserController getUserController() {
        FeedService feedService = new FeedService() {
            @Override
            public void addEvent(FeedEvent event) {
            }

            @Override
            public Collection<FeedEvent> getFeedByUserId(Long userId) {
                throw new UnsupportedOperationException();
            }
        };

        MpaRatingStorage mpaRatingStorage = new MpaRatingStorage() {
            @Override
            public Collection<MpaRating> getAll() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Optional<MpaRating> getById(Long id) {
                throw new UnsupportedOperationException();
            }
        };

        FilmMapper filmMapper = getFilmMapper(mpaRatingStorage);
        UserService userService = new UserService(new InMemoryUserStorage(), new InMemoryFilmStorage(), new UserMapper(),
                filmMapper, feedService);
        return new UserController(userService, feedService);
    }

    private static FilmMapper getFilmMapper(MpaRatingStorage mpaRatingStorage) {
        GenreStorage genreStorage = new GenreStorage() {
            @Override
            public Collection<Genre> getAll() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Optional<Genre> getById(Long id) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Collection<Genre> getByFilmId(Long filmId) {
                throw new UnsupportedOperationException();
            }
        };

        DirectorStorage directorStorage = new DirectorStorage() {
            @Override
            public Collection<Director> getAll() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Optional<Director> getById(Long id) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Director add(Director director) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void delete(Long id) {
            }

            @Override
            public Director update(Director director) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Collection<Director> getByFilmId(Long filmId) {
                throw new UnsupportedOperationException();
            }
        };

        return new FilmMapper(mpaRatingStorage, genreStorage, directorStorage);
    }
}
