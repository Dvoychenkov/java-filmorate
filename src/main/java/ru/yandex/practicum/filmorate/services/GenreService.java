package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.ValidationUtils.requireFound;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;
    private final GenreMapper genreMapper;

    public Collection<GenreDto> getAll() {
        Collection<Genre> genres = genreStorage.getAll();
        log.info("Получено {} жанров", genres.size());
        return genres.stream()
                .map(genreMapper::mapToGenreDto)
                .toList();
    }

    public GenreDto getGenre(Long id) {
        return genreMapper.mapToGenreDto(getGenreOrThrow(id));
    }

    public Genre getGenreOrThrow(Long id) {
        Genre genre = requireFound(genreStorage.getById(id), () -> "Жанр с ID " + id + " не найден");
        log.info("Получен жанр по ID {}: {}", id, genre);
        return genre;
    }
}
