package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private long idCounter = 1;

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        film.setId(++idCounter);
        films.put(film.getId(), film);
        return new Film();
    }

    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null || !films.containsKey(newFilm.getId())) {
            return null;
        }
        films.put(newFilm.getId(), newFilm);
        return new Film();
    }
}
