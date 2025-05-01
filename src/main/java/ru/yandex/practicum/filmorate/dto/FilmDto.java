package ru.yandex.practicum.filmorate.dto;

import lombok.Value;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.List;

@Value
public class FilmDto {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
    MpaRating mpaRating;
    List<Genre> genres;
}
