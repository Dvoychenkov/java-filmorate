package ru.yandex.practicum.filmorate.storage.recomendation;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmRowMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationDbStorage implements RecommendationStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    @Override
    public List<Film> getRecommendations(Long userId) {
        String query = """
                    SELECT f.*
                    FROM films f
                    WHERE f.id IN (
                    -- Подзапрос: получаем список film_id, которые понравились пользователю, выбранному по условию ниже
                        SELECT ful1.film_id
                        FROM films_users_likes ful1
                        WHERE ful1.user_id = (
                        -- Внутренний подзапрос: ищем user_id другого пользователя, который наиболее активно ставит лайки на фильмы, которые понравились исходному пользователю
                            SELECT ful2.user_id
                            FROM films_users_likes ful2
                            WHERE ful2.user_id != ? -- исключая текущего пользователя
                                AND ful2.film_id IN (
                                -- Получаем список фильмов, которые понравились исходному пользователю
                                    SELECT film_id
                                    FROM films_users_likes
                                    WHERE user_id = ? -- текущий пользователь
                                )
                            GROUP BY ful2.user_id -- группируем по пользователю для подсчета лайков каждого пользователя
                            ORDER BY COUNT(*) DESC -- сортируем по количеству совпадений (лайков на одни и те же фильмы)
                            LIMIT 1
                        )
                    )
                    -- Исключаем из результата фильмы, которые уже понравились текущему пользователю
                    AND f.id NOT IN (
                        SELECT film_id
                        FROM films_users_likes
                        WHERE user_id = ? -- текущий пользователь
                    );
                """;

        return jdbcTemplate.query(query, new Object[]{userId, userId, userId}, filmRowMapper);
    }
}