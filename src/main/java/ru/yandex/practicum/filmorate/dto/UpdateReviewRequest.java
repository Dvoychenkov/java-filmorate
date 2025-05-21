package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReviewRequest {
    @NotNull(message = "ID отзыва обязателен для обновления")
    private Long reviewId;
    @NotBlank(message = "Пустой отзыв")
    private String content;
    @NotNull(message = "Не указан тип отзыва")
    private Boolean isPositive;
}
