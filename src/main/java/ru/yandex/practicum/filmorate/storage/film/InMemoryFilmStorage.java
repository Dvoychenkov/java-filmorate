package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long idCounter = 1;

    @Override
    public Collection<Film> getAll() {
        log.info("Получено {} фильмов", films.size());
        return films.values();
    }

    @Override
    public Film add(Film film) {
        film.setId(idCounter++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм с ID {}: {}. Всего фильмов: {}", film.getId(), film, films.size());
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.info("Обновлён фильм с ID {}: {}", film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> getById(Long id) {
        Optional<Film> optFilm = Optional.ofNullable(films.get(id));
        optFilm.ifPresentOrElse(
                (film) -> log.info("Фильм с ID {} найден: {}", id, film),
                () -> log.info("Фильм с ID {} не найден", id)
        );
        return optFilm;
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        Optional<Film> optFilm = getById(filmId);
        if (optFilm.isEmpty()) return false;

        Film film = optFilm.get();
        Set<Long> filmLikesUsersIds = film.getLikesUsersIds();
        return filmLikesUsersIds.add(userId);
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        Optional<Film> optFilm = getById(filmId);
        if (optFilm.isEmpty()) return false;

        Film film = optFilm.get();
        Set<Long> filmLikesUsersIds = film.getLikesUsersIds();
        return filmLikesUsersIds.remove(userId);
    }

    @Override
    public Collection<Film> getTopFilmsByLikes(int count) {
        Comparator<Film> filmTopByLikesComparator = Comparator.comparingInt(Film::getLikesUsersIdsSize).reversed();
        return getAll().stream()
                .filter(Objects::nonNull)
                .sorted(filmTopByLikesComparator)
                .limit(count)
                .toList();
    }

    @Override
    public void removeFilm(Long id) {
        log.info("Удаление фильма с ID {}", id);
        if (id == null || !films.containsKey(id)) {
            log.warn("Фильм с ID {} не найден", id);
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        films.remove(id);
        log.info("Фильм с ID {} удален", id);
    }
}