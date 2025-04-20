package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public void addLike(Long filmId, Long userId) {
        // Проверка на наличие пользователя
        userService.getUser(userId);

        Film film = getFilm(filmId);
        Set<Long> filmLikesUsersIds = film.getLikesUsersIds();
        filmLikesUsersIds.add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
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
            throw new ValidationException("Фильм с ID " + id + " не найден");
        }
        return film;
    }
}