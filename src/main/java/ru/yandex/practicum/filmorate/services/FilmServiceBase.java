package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;
import ru.yandex.practicum.filmorate.model.enums.SearchByField;
import ru.yandex.practicum.filmorate.model.enums.SortOption;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

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
        Film createdFilm = filmStorage.add(filmMapper.mapToFilm(newRequestFilm));
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
            log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        } else {
            log.info("Пользователь {} уже ставил лайк фильму {}", userId, filmId);
        }

        feedService.addEvent(new FeedEvent(userId, FeedEventType.LIKE, FeedOperation.ADD, filmId));
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        getFilmOrThrow(filmId); // Проверка на наличие фильма
        userService.getUserOrThrow(userId); // Проверка на наличие пользователя

        boolean removed = filmStorage.removeLike(filmId, userId);
        if (removed) {
            log.info("Пользователь {} убрал лайк с фильма {}", userId, filmId);
            feedService.addEvent(new FeedEvent(userId, FeedEventType.LIKE, FeedOperation.REMOVE, filmId));
        } else {
            log.info("Пользователь {} не ставил лайк фильму {}", userId, filmId);
        }
    }

    @Override
    public Collection<FilmDto> getTopFilmsByLikes(int filmsLimit, Integer genreId, Integer year) {
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
        Film film = requireFound(filmStorage.getById(id), () -> "Фильм с ID " + id + " не найден");
        log.info("Получен фильм по ID {}: {}", id, film);
        return film;
    }

    @Override
    public Collection<FilmDto> getDirectorFilms(Long directorId, SortOption sortBy) {
        directorService.getDirectorOrThrow(directorId); // Проверка на наличие режиссёра

        Collection<Film> directorFilms;
        switch (sortBy) {
            case SortOption.YEAR -> directorFilms = filmStorage.getDirectorFilmsSortedByYears(directorId);
            case SortOption.LIKES -> directorFilms = filmStorage.getDirectorFilmsSortedByLikes(directorId);
            default -> throw new UnsupportedOperationException();
        }

        log.info("ID фильмов режиссера с сортировкой {} по годам: {}",
                sortBy.name(), directorFilms.stream().map(Film::getId));
        return directorFilms.stream()
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

    public Collection<FilmDto> searchFilms(String query, Set<SearchByField> by) {
        Set<String> searchFields = by.stream()
                .map(field -> switch (field) {
                    case TITLE -> "title";
                    case DIRECTOR -> "director";
                })
                .collect(Collectors.toSet());

        Collection<Film> films = filmStorage.searchFilms(query, searchFields);
        log.info("Найдено {} фильмов по запросу '{}' и полям: {}", films.size(), query, searchFields);

        return films.stream()
                .map(filmMapper::mapToFilmDto)
                .toList();
    }
}