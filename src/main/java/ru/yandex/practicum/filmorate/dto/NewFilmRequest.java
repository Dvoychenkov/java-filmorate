package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValid;

import java.time.LocalDate;
import java.util.List;

@Data
public class NewFilmRequest {
    @NotBlank(message = "Пустое название фильма")
    private String name;

    @NotBlank(message = "Пустое описание фильма")
    @Size(max = 200, message = "Описание фильма превышает допустимое количество символов")
    private String description;

    @NotNull(message = "Пустая дата релиза фильма")
    @ReleaseDateValid
    private LocalDate releaseDate;

    @NotNull(message = "Пустая продолжительность фильма")
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;

    @NotNull(message = "Не указан рейтинг MPA")
    private MpaRating mpa;

    private List<Genre> genres;

    @NotNull(message = "Не указан режиссер")
    private Director director;
}
