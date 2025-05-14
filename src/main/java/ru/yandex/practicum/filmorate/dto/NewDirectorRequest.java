package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewDirectorRequest {
    @NotBlank(message = "Пустое имя режиссера")
    private String name;
}
