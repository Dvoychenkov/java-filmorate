package ru.yandex.practicum.filmorate.util;

import com.github.javafaker.Faker;
import ru.yandex.practicum.filmorate.model.*;

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

    public static Film generateFilm(MpaRating mpaRating, Director director) {
        return generateFilm(mpaRating, director, List.of());
    }

    public static Film generateFilm(MpaRating mpaRating, Director director, List<Genre> genres) {
        return new Film(
                null,
                faker.book().title(),
                faker.lorem().sentence(10, 10),
                generatePastDate(),
                faker.number().numberBetween(60, 180),
                mpaRating,
                director,
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
}
