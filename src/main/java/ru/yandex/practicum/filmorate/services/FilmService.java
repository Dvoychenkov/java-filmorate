package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    Collection<FilmDto> getAll();

    FilmDto create(NewFilmRequest newRequestFilm);

    FilmDto update(UpdateFilmRequest updateRequestFilm);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Collection<FilmDto> getTopFilmsByLikes(int filmsLimit, Integer genreId, Integer year);

    FilmDto getFilm(Long id);

    Film getFilmOrThrow(Long id);

    Collection<FilmDto> getDirectorFilmsSortedByLikes(Long directorId);

    Collection<FilmDto> getDirectorFilmsSortedByYears(Long directorId);

    Collection<FilmDto> getCommonFilms(Long userId, Long friendId);

    void removeFilm(Long id);

    Collection<FilmDto> searchFilms(String query, String by);
}
