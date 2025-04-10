package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();
    private long idCounter = 1;
    private static final LocalDate FILM_B_DAY = LocalDate.of(1895, 12, 28);
    private static final int MAX_FILM_DESC_LEN = 200;

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateForCreate(film);
        film.setId(idCounter++);
        films.put(film.getId(), film);
        log.info("Создан фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null || !films.containsKey(newFilm.getId())) {
            throw new ValidationException("Фильм с указанным ID не найден");
        }

        Film existingFilm = films.get(newFilm.getId());
        validateForUpdate(newFilm, existingFilm);
        log.info("Обновлён фильм: {}", existingFilm);
        return existingFilm;
    }

    private void validateForCreate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Пустое название фильма");
        }

        if (film.getDescription() == null || film.getDescription().isBlank()) {
            throw new ValidationException("Пустое описание фильма");
        }
        if (film.getDescription().length() > MAX_FILM_DESC_LEN) {
            throw new ValidationException("Описание фильма превышает допустимое количество символов");
        }

        if (film.getReleaseDate() == null) {
            throw new ValidationException("Пустая дата релиза фильма");
        }
        if (film.getReleaseDate().isBefore(FILM_B_DAY)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() == null) {
            throw new ValidationException("Пустая продолжительность фильма");
        }
        if (film.getDuration() < 1) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    private void validateForUpdate(Film newFilm, Film existingFilm) {
        if (newFilm.getName() != null) {
            if (newFilm.getName().isBlank()) {
                throw new ValidationException("Пустое название фильма");
            }
            existingFilm.setName(newFilm.getName());
        }

        if (newFilm.getDescription() != null) {
            if (newFilm.getDescription().isBlank()) {
                throw new ValidationException("Пустое описание фильма");
            }
            if (newFilm.getDescription().length() > MAX_FILM_DESC_LEN) {
                throw new ValidationException("Описание фильма превышает допустимое количество символов");
            }
            existingFilm.setDescription(newFilm.getDescription());
        }

        if (newFilm.getReleaseDate() != null) {
            if (newFilm.getReleaseDate().isBefore(FILM_B_DAY)) {
                throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
            }
            existingFilm.setReleaseDate(newFilm.getReleaseDate());
        }

        if (newFilm.getDuration() != null) {
            if (newFilm.getDuration() < 0) {
                throw new ValidationException("Продолжительность фильма должна быть положительной");
            }
            existingFilm.setDuration(newFilm.getDuration());
        }
    }
}
