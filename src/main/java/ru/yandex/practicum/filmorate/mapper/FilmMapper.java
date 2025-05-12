package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;
import ru.yandex.practicum.filmorate.validation.ValidationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FilmMapper {
    private final MpaRatingStorage mpaRatingStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    public Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpa(resolveMpaRating(request.getMpa()));
        film.setGenres(resolveGenres(request.getGenres()));
        film.setDirector(resolveDirector(request.getDirector()));
        return film;
    }

    public void updateFilmFromRequest(Film film, UpdateFilmRequest request) {
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpa(resolveMpaRating(request.getMpa()));
        film.setGenres(resolveGenres(request.getGenres()));
        film.setDirector(resolveDirector(request.getDirector()));
    }

    public FilmDto mapToFilmDto(Film film) {
        return new FilmDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa(),
                new ArrayList<>(film.getGenres()),
                film.getDirector()
        );
    }

    private MpaRating resolveMpaRating(MpaRating mpa) {
        if (mpa == null || mpa.getId() == null) return null;
        return ValidationUtils.requireFound(
                mpaRatingStorage.getById(mpa.getId()),
                () -> "MPA рейтинг с ID " + mpa.getId() + " не найден"
        );
    }

    private Director resolveDirector(Director director) {
        if (director == null || director.getId() == null) return null;
        return ValidationUtils.requireFound(
                directorStorage.getById(director.getId()),
                () -> "Директор с ID " + director.getId() + " не найден"
        );
    }

    private List<Genre> resolveGenres(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) return List.of();
        return genres.stream()
                .filter(Objects::nonNull)
                .map(genre -> ValidationUtils.requireFound(
                                genreStorage.getById(genre.getId()),
                                () -> "Жанр с ID " + genre.getId() + " не найден"
                        )
                )
                .distinct()
                .toList();
    }
}
