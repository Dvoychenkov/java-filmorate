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
                        SELECT ful1.film_id
                        FROM films_users_likes ful1
                        WHERE ful1.user_id = (
                            SELECT ful2.user_id
                            FROM films_users_likes ful2
                            WHERE ful2.user_id != ?
                                AND ful2.film_id IN (
                                    SELECT film_id
                                    FROM films_users_likes
                                    WHERE user_id = ?
                                )
                            GROUP BY ful2.user_id
                            ORDER BY COUNT(*) DESC
                            LIMIT 1
                        )
                    )
                    AND f.id NOT IN (
                        SELECT film_id
                        FROM films_users_likes
                        WHERE user_id = ?
                    );
                """;

        return jdbcTemplate.query(query, new Object[]{userId, userId, userId}, filmRowMapper);
    }
}