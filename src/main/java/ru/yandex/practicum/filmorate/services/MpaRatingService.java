package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.ValidationUtils.requireFound;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaRatingService {
    private final MpaRatingStorage mpaRatingStorage;
    private final MpaRatingMapper mpaRatingMapper;

    public Collection<MpaRatingDto> getAll() {
        Collection<MpaRating> mpaRatings = mpaRatingStorage.getAll();
        log.info("Получено {} рейтингов MPA", mpaRatings.size());
        return mpaRatings.stream()
                .map(mpaRatingMapper::mapToMpaRatingDto)
                .toList();
    }

    public MpaRatingDto getMpaRating(Long id) {
        return mpaRatingMapper.mapToMpaRatingDto(getMpaRatingOrThrow(id));
    }

    public MpaRating getMpaRatingOrThrow(Long id) {
        MpaRating mpaRating = requireFound(mpaRatingStorage.getById(id), () -> "MPA рейтинг с ID " + id + " не найден");
        log.info("Получен MPA рейтинг по ID {}: {}", id, mpaRating);
        return mpaRating;
    }
}