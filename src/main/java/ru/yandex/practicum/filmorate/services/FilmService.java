package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
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
    private final FilmMapper filmMapper;

    public Collection<FilmDto> getAll() {
        Collection<Film> films = filmStorage.getAll();
        log.info("Получено {} фильмов", films.size());
        return films.stream()
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto create(NewFilmRequest newRequestFilm) {
        Film filmToCreate = filmMapper.mapToFilm(newRequestFilm);
        Film createdFilm = filmStorage.add(filmToCreate);
        if (createdFilm == null) throw new IllegalStateException("Не удалось сохранить данные для нового фильма");
        log.info("Создан фильм: {}", createdFilm);
        return filmMapper.mapToFilmDto(createdFilm);
    }

    public FilmDto update(UpdateFilmRequest updateRequestFilm) {
        Film filmToUpdate = getFilmOrThrow(updateRequestFilm.getId());
        filmMapper.updateFilmFromRequest(filmToUpdate, updateRequestFilm);
        Film updatedFilm = filmStorage.update(filmToUpdate);
        log.info("Обновлён фильм: {}", updatedFilm);
        return filmMapper.mapToFilmDto(updatedFilm);
    }

    public void addLike(Long filmId, Long userId) {
        getFilmOrThrow(filmId); // Проверка на наличие фильма
        userService.getUserOrThrow(userId); // Проверка на наличие пользователя

        boolean added = filmStorage.addLike(filmId, userId);
        if (added) {
            log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        } else {
            log.info("Пользователь {} уже ставил лайк фильму {}", userId, filmId);
        }
    }

    public void removeLike(Long filmId, Long userId) {
        getFilmOrThrow(filmId); // Проверка на наличие фильма
        userService.getUserOrThrow(userId); // Проверка на наличие пользователя

        boolean removed = filmStorage.removeLike(filmId, userId);
        if (removed) {
            log.info("Пользователь {} убрал лайк с фильма {}", userId, filmId);
        } else {
            log.info("Пользователь {} не ставил лайк фильму {}", userId, filmId);
        }
    }

    public Collection<FilmDto> getTopFilmsByLikes(int filmsLimit) {
        if (filmsLimit <= 0) {
            log.warn("Передан некорректный параметр count = {}, используется значение по умолчанию", filmsLimit);
            filmsLimit = 10;
        }

        Collection<Film> top = filmStorage.getTopFilmsByLikes(filmsLimit);
        log.info("Возвращён топ {} фильмов по лайкам", top.size());
        log.info("ID фильмов из топа: {}", top.stream().map(Film::getId).toList());
        return top.stream()
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto getFilm(Long id) {
        return filmMapper.mapToFilmDto(getFilmOrThrow(id));
    }

    public Film getFilmOrThrow(Long id) {
        if (id == null) throw new ValidationException("Некорректный ID фильма");
        Film film = requireFound(filmStorage.getById(id), () -> "Фильм с ID " + id + " не найден");
        log.info("Получен фильм по ID {}: {}", id, film);
        return film;
    }

    public void removeFilm(Long id) {
        filmStorage.removeFilm(id);
        log.info("Фильм с ID {} удалён", id);
    }
}