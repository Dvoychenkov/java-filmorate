package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.ValidationUtils.requireFound;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Collection<Film> getAll() {
        Collection<Film> films = filmStorage.getAll();
        log.info("Получено {} фильмов", films.size());
        return films;
    }

    public Film create(Film film) {
        Film created = filmStorage.add(film);
        if (created == null) throw new IllegalStateException("Не удалось сохранить данные для нового фильма");
        log.info("Создан фильм: {}", created);
        return created;
    }

    public Film update(Film film) {
        getFilm(film.getId()); // Проверка на наличие фильма

        Film updated = filmStorage.update(film);
        log.info("Обновлён фильм: {}", updated);
        return updated;
    }

    public void addLike(Long filmId, Long userId) {
        userService.getUser(userId); // Проверка на наличие пользователя
        Film film = getFilm(filmId); // Проверка на наличие фильма

        boolean added = filmStorage.addLike(filmId, userId);
        if (added) {
            log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        } else {
            log.info("Пользователь {} уже ставил лайк фильму {}", userId, filmId);
        }
    }

    public void removeLike(Long filmId, Long userId) {
        userService.getUser(userId); // Проверка на наличие пользователя
        Film film = getFilm(filmId); // Проверка на наличие фильма

        boolean removed = filmStorage.removeLike(filmId, userId);
        if (removed) {
            log.info("Пользователь {} убрал лайк с фильма {}", userId, filmId);
        } else {
            log.info("Пользователь {} не ставил лайк фильму {}", userId, filmId);
        }
    }

    public Collection<Film> getTopFilmsByLikes(int filmsLimit) {
        if (filmsLimit <= 0) {
            log.warn("Передан некорректный параметр count = {}, используется значение по умолчанию", filmsLimit);
            filmsLimit = 10;
        }

        Collection<Film> top = filmStorage.getTopFilmsByLikes(filmsLimit);
        log.info("Возвращён топ {} фильмов по лайкам", top.size());
        log.info("ID фильмов из топа: {}", top.stream().map(Film::getId).toList());
        return top;
    }

    public Film getFilm(Long id) {
        if (id == null) throw new ValidationException("Некорректный ID фильма");
        Film film = requireFound(filmStorage.getById(id), () -> "Фильм с ID " + id + " не найден");
        log.info("Получен фильм по ID {}: {}", id, film);
        return film;
    }
}