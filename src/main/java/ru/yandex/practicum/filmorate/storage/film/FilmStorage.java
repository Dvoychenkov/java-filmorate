package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAll();

    Optional<Film> getById(Long id);

    Film add(Film film);

    Film update(Film film);

    boolean addLike(Long filmId, Long userId);

    boolean removeLike(Long filmId, Long userId);

    Collection<Film> getTopFilmsByLikes(int count, Integer genreId, Integer year);

    void removeFilm(Long id);

}
