package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        // Проверка на наличие фильма
        getFilm(film.getId());

        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        // Проверка на наличие пользователя
        userService.getUser(userId);

        Film film = getFilm(filmId);
        Set<Long> filmLikesUsersIds = film.getLikesUsersIds();
        filmLikesUsersIds.add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        // Проверка на наличие пользователя
        userService.getUser(userId);
        
        Film film = getFilm(filmId);
        Set<Long> filmLikesUsersIds = film.getLikesUsersIds();
        filmLikesUsersIds.remove(userId);
    }

    public List<Film> getTopFilmsByLikes(int filmsLimit) {
        if (filmsLimit <= 0) {
            filmsLimit = 10;
        }

        Comparator<Film> filmTopByLikesComparator = Comparator.comparingInt(Film::getLikesUsersIdsSize).reversed();
        return filmStorage.getAll().stream()
                .filter(Objects::nonNull)
                .sorted(filmTopByLikesComparator)
                .limit(filmsLimit)
                .toList();
    }

    public Film getFilm(Long id) {
        if (id == null) {
            throw new ValidationException("Некорректный ID фильма");
        }

        Film film = filmStorage.getById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }
        return film;
    }
}