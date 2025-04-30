package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
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

    private final Set<Long> likesUsersIds = new HashSet<>();

    private final List<Genre> genres = new ArrayList<>();

    private MpaRating mpaRating;

    public int getLikesUsersIdsSize() {
        return likesUsersIds.size();
    }
}