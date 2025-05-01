package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Component
public class MpaRatingMapper {
    public MpaRatingDto mapToMpaRatingDto(MpaRating rating) {
        return new MpaRatingDto(rating.getId(), rating.getName());
    }
}