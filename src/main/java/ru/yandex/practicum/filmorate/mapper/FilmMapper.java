package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
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

    public Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpaRating(resolveMpaRating(request.getMpaRatingId()));
        film.setGenres(resolveGenres(request.getGenreIds()));
        return film;
    }

    public void updateFilmFromRequest(Film film, UpdateFilmRequest request) {
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpaRating(resolveMpaRating(request.getMpaRatingId()));
        film.setGenres(resolveGenres(request.getGenreIds()));
    }

    public FilmDto mapToFilmDto(Film film) {
        return new FilmDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRating(),
                new ArrayList<>(film.getGenres())
        );
    }

    private MpaRating resolveMpaRating(Long id) {
        return ValidationUtils.requireFound(
                mpaRatingStorage.getById(id),
                () -> "MPA рейтинг с ID " + id + " не найден"
        );
    }

    private List<Genre> resolveGenres(List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) return List.of();
        return genreIds.stream()
                .filter(Objects::nonNull)
                .map(id -> ValidationUtils.requireFound(
                                genreStorage.getById(id),
                                () -> "Жанр с ID " + id + " не найден"
                        )
                )
                .distinct()
                .toList();
    }
}
