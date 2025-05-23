package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Collection<Film> getAll();

    Optional<Film> getById(Long id);

    Film add(Film film);

    Film update(Film film);

    boolean addLike(Long filmId, Long userId);

    boolean removeLike(Long filmId, Long userId);

    Collection<Film> getTopFilmsByLikes(int count, Integer genreId, Integer year);

    Collection<Film> getDirectorFilmsSortedByYears(Long directorId);

    Collection<Film> getDirectorFilmsSortedByLikes(Long directorId);

    Collection<Film> getCommonFilms(Long userId, Long friendId);

    void removeFilm(Long id);

    Collection<Film> getFilmsRecommendations(Long userId);

    Collection<Film> searchFilms(String query, Set<String> by);

}