package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private FilmController controller;

    @BeforeEach
    void contextLoads() {
        controller = new FilmController();
    }

    @Test
    void shouldThrowWhenNameIsIncorrect() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);

        ValidationException ex = assertThrows(ValidationException.class, () -> controller.create(film));
        assertEquals("Пустое название фильма", ex.getMessage());

        film.setName(null);
        ex = assertThrows(ValidationException.class, () -> controller.create(film));
        assertEquals("Пустое название фильма", ex.getMessage());

        film.setName("Фильм");
        assertDoesNotThrow(() -> controller.create(film), "Валидация должна проходить успешно");
    }

    @Test
    void shouldThrowWhenDescriptionIsIncorrect() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);

        ValidationException ex = assertThrows(ValidationException.class, () -> controller.create(film));
        assertEquals("Описание фильма превышает допустимое количество символов", ex.getMessage());

        film.setDescription("A".repeat(200));
        assertDoesNotThrow(() -> controller.create(film), "Валидация должна проходить успешно");

        film.setDescription("A".repeat(199));
        assertDoesNotThrow(() -> controller.create(film), "Валидация должна проходить успешно");

        film.setDescription(null);
        ex = assertThrows(ValidationException.class, () -> controller.create(film));
        assertEquals("Пустое описание фильма", ex.getMessage());

        film.setDescription(" ");
        ex = assertThrows(ValidationException.class, () -> controller.create(film));
        assertEquals("Пустое описание фильма", ex.getMessage());
    }

    @Test
    void shouldThrowWhenReleaseDateIsIncorrect() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(90);

        ValidationException ex = assertThrows(ValidationException.class, () -> controller.create(film));
        assertEquals("Дата релиза фильма не может быть раньше 28 декабря 1895 года", ex.getMessage());

        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> controller.create(film), "Валидация должна проходить успешно");

        film.setReleaseDate(LocalDate.of(1895, 12, 29));
        assertDoesNotThrow(() -> controller.create(film), "Валидация должна проходить успешно");

        film.setReleaseDate(null);
        ex = assertThrows(ValidationException.class, () -> controller.create(film));
        assertEquals("Пустая дата релиза фильма", ex.getMessage());
    }

    @Test
    void shouldThrowWhenDurationIsIncorrect() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2025, 4, 10));
        film.setDuration(0);

        ValidationException ex = assertThrows(ValidationException.class, () -> controller.create(film));
        assertEquals("Продолжительность фильма должна быть положительной", ex.getMessage());

        film.setDuration(-1);
        ex = assertThrows(ValidationException.class, () -> controller.create(film));
        assertEquals("Продолжительность фильма должна быть положительной", ex.getMessage());

        film.setDuration(null);
        ex = assertThrows(ValidationException.class, () -> controller.create(film));
        assertEquals("Пустая продолжительность фильма", ex.getMessage());
    }

    @Test
    void shouldThrowWhenFilmIsNull() {
        assertThrows(NullPointerException.class, () -> controller.create(null));
    }

    @Test
    void shouldThrowWhenFilmIsEmpty() {
        Film film = new Film();
        assertThrows(ValidationException.class, () -> controller.create(film));
    }
}
