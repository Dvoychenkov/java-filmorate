package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.enums.SearchByField;
import ru.yandex.practicum.filmorate.model.enums.SortOption;
import ru.yandex.practicum.filmorate.services.FilmService;

import java.util.Collection;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{filmId}")
    public FilmDto getById(@PathVariable Long filmId) {
        return filmService.getFilm(filmId);
    }

    @PostMapping
    public FilmDto create(@Valid @RequestBody NewFilmRequest film) {
        FilmDto created = filmService.create(film);
        log.info("Создан фильм: {}", created);
        return created;
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest film) {
        FilmDto updated = filmService.update(film);
        log.info("Обновлён фильм: {}", updated);
        return updated;
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.removeLike(filmId, userId);
        log.info("Пользователь {} убрал лайк с фильма {}", userId, filmId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getPopular(@RequestParam(defaultValue = "10") int count,
                                          @RequestParam(name = "genreId", required = false) Integer genreId,
                                          @RequestParam(name = "year", required = false) Integer year) {
        return filmService.getTopFilmsByLikes(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public Collection<FilmDto> getDirectorFilms(@PathVariable Long directorId, @RequestParam SortOption sortBy) {
        if (sortBy == SortOption.YEAR) {
            return filmService.getDirectorFilmsSortedByYears(directorId);
        } else if (sortBy == SortOption.LIKES) {
            return filmService.getDirectorFilmsSortedByLikes(directorId);
        }
        throw new UnsupportedOperationException();
    }

    @GetMapping("/common")
    public Collection<FilmDto> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @DeleteMapping("/{filmId}")
    public void removeFilm(@PathVariable Long filmId) {
        filmService.removeFilm(filmId);
        log.info("Фильм с ID {} удалён", filmId);
    }

    @GetMapping("/search")
    public Collection<FilmDto> searchFilms(@RequestParam String query, @RequestParam Set<SearchByField> by) {
        log.info("Поиск фильмов по запросу '{}' по полям: {}", query, by);
        return filmService.searchFilms(query, by);
    }

}