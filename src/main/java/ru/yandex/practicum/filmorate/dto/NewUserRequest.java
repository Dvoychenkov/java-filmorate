package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest implements UserRequest {
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
}