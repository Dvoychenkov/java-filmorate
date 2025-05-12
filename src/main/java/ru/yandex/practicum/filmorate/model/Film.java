package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @NotNull(message = "Не указан рейтинг MPA")
    private MpaRating mpa;

    private Director director;

    private List<Genre> genres = new ArrayList<>();

    private Set<Long> likesUsersIds = new HashSet<>();

    public int getLikesUsersIdsSize() {
        return likesUsersIds.size();
    }
}