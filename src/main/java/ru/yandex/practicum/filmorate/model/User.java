package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class User {
    private Long id;

    @NotBlank(message = "Пустой e-mail пользователя")
    @Email(message = "Некорректный e-mail пользователя")
    private String email;

    @NotBlank(message = "Пустой логин пользователя")
    @Pattern(regexp = "^\\S+$", message = "Логин пользователя содержит пробелы")
    private String login;

    private String name;

    @NotNull(message = "Пустая дата рождения пользователя")
    @PastOrPresent(message = "Дата рождения пользователя больше текущей даты")
    private LocalDate birthday;

    private final Map<Long, FriendshipStatus> friends = new HashMap<>();
}
