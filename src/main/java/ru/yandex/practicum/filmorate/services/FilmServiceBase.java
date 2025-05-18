package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validation.ValidationUtils.requireFound;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceBase implements FilmService {
    private final FilmStorage filmStorage;
    private final FilmMapper filmMapper;
    private final UserService userService;
    private final FeedService feedService;
    private final DirectorService directorService;

    @Override
    public Collection<FilmDto> getAll() {
        Collection<Film> films = filmStorage.getAll();
        log.info("Получено {} фильмов", films.size());
        return films.stream()
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    @Override
    public FilmDto create(NewFilmRequest newRequestFilm) {
        Film filmToCreate = filmMapper.mapToFilm(newRequestFilm);
        Film createdFilm = filmStorage.add(filmToCreate);
        if (createdFilm == null) throw new IllegalStateException("Не удалось сохранить данные для нового фильма");
        log.info("Создан фильм: {}", createdFilm);
        return filmMapper.mapToFilmDto(createdFilm);
    }

    @Override
    public FilmDto update(UpdateFilmRequest updateRequestFilm) {
        Film filmToUpdate = getFilmOrThrow(updateRequestFilm.getId());
        filmMapper.updateFilmFromRequest(filmToUpdate, updateRequestFilm);
        Film updatedFilm = filmStorage.update(filmToUpdate);
        log.info("Обновлён фильм: {}", updatedFilm);
        return filmMapper.mapToFilmDto(updatedFilm);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        getFilmOrThrow(filmId); // Проверка на наличие фильма
        userService.getUserOrThrow(userId); // Проверка на наличие пользователя

        boolean added = filmStorage.addLike(filmId, userId);
        if (added) {
            FeedEvent feedEvent = new FeedEvent(userId, FeedEventType.LIKE, FeedOperation.ADD, filmId);
            feedService.addEvent(feedEvent);
            log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        } else {
            log.info("Пользователь {} уже ставил лайк фильму {}", userId, filmId);
        }
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        getFilmOrThrow(filmId); // Проверка на наличие фильма
        userService.getUserOrThrow(userId); // Проверка на наличие пользователя

        boolean removed = filmStorage.removeLike(filmId, userId);
        if (removed) {
            FeedEvent feedEvent = new FeedEvent(userId, FeedEventType.LIKE, FeedOperation.REMOVE, filmId);
            feedService.addEvent(feedEvent);
            log.info("Пользователь {} убрал лайк с фильма {}", userId, filmId);
        } else {
            log.info("Пользователь {} не ставил лайк фильму {}", userId, filmId);
        }
    }

    @Override
    public Collection<FilmDto> getTopFilmsByLikes(int filmsLimit, Integer genreId, Integer year) {
        if (filmsLimit <= 0) {
            log.warn("Передан некорректный параметр count = {}, используется значение по умолчанию", filmsLimit);
            filmsLimit = 10;
        }

        Collection<Film> top = filmStorage.getTopFilmsByLikes(filmsLimit, genreId, year);
        log.info("Возвращён топ {} фильмов по лайкам", top.size());
        log.info("ID фильмов из топа: {}", top.stream().map(Film::getId).toList());
        return top.stream()
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    @Override
    public FilmDto getFilm(Long id) {
        return filmMapper.mapToFilmDto(getFilmOrThrow(id));
    }

    @Override
    public Film getFilmOrThrow(Long id) {
        if (id == null) throw new ValidationException("Некорректный ID фильма");
        Film film = requireFound(filmStorage.getById(id), () -> "Фильм с ID " + id + " не найден");
        log.info("Получен фильм по ID {}: {}", id, film);
        return film;
    }

    @Override
    public Collection<FilmDto> getDirectorFilmsSortedByLikes(Long directorId) {
        directorService.getDirectorOrThrow(directorId);
        Collection<Film> directorFilmsSortedByLikes = filmStorage.getDirectorFilmsSortedByLikes(directorId);
        log.info("ID фильмов режиссера по лайкам: {}", directorFilmsSortedByLikes.stream().map(Film::getId).toList());
        return directorFilmsSortedByLikes.stream()
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    @Override
    public Collection<FilmDto> getDirectorFilmsSortedByYears(Long directorId) {
        directorService.getDirectorOrThrow(directorId);
        Collection<Film> directorFilmsSortedByYears = filmStorage.getDirectorFilmsSortedByYears(directorId);
        log.info("ID фильмов режиссера по годам: {}", directorFilmsSortedByYears.stream().map(Film::getId).toList());
        return directorFilmsSortedByYears.stream()
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    @Override
    public Collection<FilmDto> getCommonFilms(Long userId, Long friendId) {
        userService.getUserOrThrow(userId); // Проверка на наличие пользователя
        userService.getUserOrThrow(friendId); // Проверка на наличие друга

        Collection<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);
        Collection<FilmDto> commonFilmsList = commonFilms.stream()
                .map(filmMapper::mapToFilmDto)
                .toList();
        log.info("Общие фильмы для {} и {}: {}", userId, friendId, commonFilmsList);
        return commonFilmsList;
    }

    @Override
    public void removeFilm(Long id) {
        filmStorage.removeFilm(id);
        log.info("Фильм с ID {} удалён", id);
    }

    @Override
    public Collection<FilmDto> searchFilms(String query, String by) {
        log.info("Поиск фильмов по запросу {} с полями: {}", query, by);
        Set<String> searchFields = Arrays.stream(by.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        Collection<Film> films = filmStorage.searchFilms(query, searchFields);
        log.info("Найдено {} фильмов по запросу {}", films.size(), query);
        return films.stream()
                .map(filmMapper::mapToFilmDto)
                .toList();
    }


}