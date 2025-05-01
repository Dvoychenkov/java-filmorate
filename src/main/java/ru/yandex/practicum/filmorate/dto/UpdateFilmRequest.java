package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValid;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateFilmRequest {
    @NotNull(message = "ID фильма обязателен для обновления")
    private Long id;

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
    private Long mpaRatingId;

    private List<Long> genreIds;
}
