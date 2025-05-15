package ru.yandex.practicum.filmorate.storage.recomendation;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface RecommendationStorage {
    List<Film> getRecommendations(Long userId);
}