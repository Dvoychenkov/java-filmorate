package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    public Film getById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            log.info("Фильм с ID {} не найден", id);
        } else {
            log.info("Фильм с ID {} найден: {}", id, film);
        }
        return film;
    }
}