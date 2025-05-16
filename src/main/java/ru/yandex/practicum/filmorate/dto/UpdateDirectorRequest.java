package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDirectorRequest {
    @NotNull(message = "ID режиссера обязателен для обновления")
    private Long id;

    @NotBlank(message = "Имя режиссера не должно быть пустым")
    private String name;
}
