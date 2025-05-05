package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

@Component
public class GenreMapper {
    public GenreDto mapToGenreDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
