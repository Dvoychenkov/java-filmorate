package ru.yandex.practicum.filmorate.dto;

import lombok.Value;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.List;

@Value
public class FilmDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private MpaRating mpa;
    private List<Genre> genres;
    private List<Director> directors;
}
