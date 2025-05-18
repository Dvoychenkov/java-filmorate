package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public interface FilmService {
    Collection<FilmDto> getAll();

<<<<<<<< HEAD:src/main/java/ru/yandex/practicum/filmorate/services/FilmServiceBase.java
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
========
    FilmDto create(NewFilmRequest newRequestFilm);

    FilmDto update(UpdateFilmRequest updateRequestFilm);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Collection<FilmDto> getTopFilmsByLikes(int filmsLimit, Integer genreId, Integer year);
>>>>>>>> origin/add-search:src/main/java/ru/yandex/practicum/filmorate/services/FilmService.java

    FilmDto getFilm(Long id);

<<<<<<<< HEAD:src/main/java/ru/yandex/practicum/filmorate/services/FilmServiceBase.java
    @Override
    public void removeLike(Long filmId, Long userId) {
        getFilmOrThrow(filmId); // Проверка на наличие фильма
        userService.getUserOrThrow(userId); // Проверка на наличие пользователя
========
    Film getFilmOrThrow(Long id);
>>>>>>>> origin/add-search:src/main/java/ru/yandex/practicum/filmorate/services/FilmService.java

    Collection<FilmDto> getDirectorFilmsSortedByLikes(Long directorId);

<<<<<<<< HEAD:src/main/java/ru/yandex/practicum/filmorate/services/FilmServiceBase.java
    @Override
    public Collection<FilmDto> getTopFilmsByLikes(int filmsLimit, Integer genreId, Integer year) {
        if (filmsLimit <= 0) {
            log.warn("Передан некорректный параметр count = {}, используется значение по умолчанию", filmsLimit);
            filmsLimit = 10;
        }
========
    Collection<FilmDto> getDirectorFilmsSortedByYears(Long directorId);
>>>>>>>> origin/add-search:src/main/java/ru/yandex/practicum/filmorate/services/FilmService.java

    Collection<FilmDto> getCommonFilms(Long userId, Long friendId);

<<<<<<<< HEAD:src/main/java/ru/yandex/practicum/filmorate/services/FilmServiceBase.java
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
========
    void removeFilm(Long id);

    Collection<FilmDto> searchFilms(String query, String by);
}
>>>>>>>> origin/add-search:src/main/java/ru/yandex/practicum/filmorate/services/FilmService.java
