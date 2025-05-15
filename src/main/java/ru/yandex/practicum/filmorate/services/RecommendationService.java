package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.recomendation.RecommendationStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationStorage recommendationStorage;

    public List<Film> getRecommendations(Long userId) {
        checkId(userId);
        log.info("Получение рекомендаций для пользователя {}", userId);
        return recommendationStorage.getRecommendations(userId);
    }

    private void checkId(Long id) {
        if (id == null) {
            log.error("Передан null или пустой id");
            throw new ValidationException("Id не может быть пустым");
        }
    }
}