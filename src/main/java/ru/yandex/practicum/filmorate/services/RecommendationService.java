package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.recomendation.RecommendationStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService implements RecommendationStorage {
    private final RecommendationStorage recommendationStorage;
    private final UserService userService;

    public List<Film> getRecommendations(Long userId) {
        userService.getUserOrThrow(userId);
        log.info("Получение рекомендаций для пользователя {}", userId);
        return recommendationStorage.getRecommendations(userId);
    }
}