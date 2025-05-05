package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {
    private Validator validator;
    private static final LocalDate FILM_B_DAY = LocalDate.of(1895, 12, 28);

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailEmptyName() {
        Film film = new Film();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));

        film.setName("");
        violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));

        film.setName(" ");
        violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldSuccessCorrectName() {
        Film film = new Film();
        film.setName("Фильм");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldFailIncorrectDescription() {
        Film film = new Film();
        film.setDescription("A".repeat(201));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));

        film.setDescription("A".repeat(9999));
        violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldFailEmptyDescription() {
        Film film = new Film();

        film.setDescription("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));

        film.setDescription(" ");
        violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldSuccessCorrectDescription() {
        Film film = new Film();

        film.setDescription("A".repeat(200));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));

        film.setDescription("A".repeat(199));
        violations = validator.validate(film);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));

        film.setDescription("A");
        violations = validator.validate(film);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldFailIncorrectReleaseDate() {
        Film film = new Film();
        film.setReleaseDate(FILM_B_DAY.minusDays(1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));

        film.setReleaseDate(FILM_B_DAY.minusDays(999));
        violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    void shouldFailEmptyReleaseDate() {
        Film film = new Film();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    void shouldSuccessCorrectReleaseDate() {
        Film film = new Film();

        film.setReleaseDate(FILM_B_DAY);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));

        film.setReleaseDate(FILM_B_DAY.plusDays(1));
        violations = validator.validate(film);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));

        film.setReleaseDate(FILM_B_DAY.plusDays(999));
        violations = validator.validate(film);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    void shouldFailIncorrectDuration() {
        Film film = new Film();

        film.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));

        film.setDuration(-1);
        violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));

        film.setDuration(-999);
        violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    void shouldFailEmptyDuration() {
        Film film = new Film();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    void shouldSuccessCorrectDuration() {
        Film film = new Film();

        film.setDuration(1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));

        film.setDuration(120);
        violations = validator.validate(film);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));

        film.setDuration(999);
        violations = validator.validate(film);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    void shouldSuccessCorrectFilm() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "G", "G", "General Audiences: Нет возрастных ограничений"));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailEmptyFilm() {
        Film film = new Film();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        Film emptyFilm = null;
        assertThrows(IllegalArgumentException.class, () -> validator.validate(emptyFilm));
    }
}
