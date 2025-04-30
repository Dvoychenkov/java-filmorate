package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAll();

    // TODO рассмотреть вариант смены на Optional<User> (правка реализаций + сервиса)
    Film getById(Long id);

    Film add(Film film);

    Film update(Film film);
}