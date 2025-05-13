package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Collection<Director> getAll();

    Optional<Director> getById(Long id);

    Director add(Director director);

    void delete(Long id);

    Director update(Director director);

    Collection<Director> getByFilmId(Long filmId);
}
