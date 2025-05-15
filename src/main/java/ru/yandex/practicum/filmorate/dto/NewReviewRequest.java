package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewReviewRequest {
    @NotNull(message = "ID фильма обязателен для создания отзыва")
    private Long filmId;
    @NotNull(message = "ID пользователя обязателен для создания отзыва")
    private Long userId;
    @NotBlank(message = "Пустой отзыв")
    private String content;
    @NotNull(message = "Не указан тип отзыва")
    private Boolean isPositive;
}