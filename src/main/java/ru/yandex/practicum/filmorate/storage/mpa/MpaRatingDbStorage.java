package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.base.BaseReadRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
@Slf4j
public class MpaRatingDbStorage extends BaseReadRepository<MpaRating> implements MpaRatingStorage {
    private static final String SQL_SELECT_ALL = "SELECT * FROM mpa_ratings";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM mpa_ratings WHERE id = ?";

    public MpaRatingDbStorage(JdbcTemplate jdbcTemplate, MpaRatingRowMapper mpaRatingRowMapper) {
        super(jdbcTemplate, mpaRatingRowMapper);
    }

    @Override
    public Collection<MpaRating> getAll() {
        Collection<MpaRating> mpaRatings = queryMany(SQL_SELECT_ALL);
        log.info("Получено {} МПА рейтингов из БД", mpaRatings.size());
        return mpaRatings;
    }

    @Override
    public Optional<MpaRating> getById(Long id) {
        Optional<MpaRating> optMpaRating = queryOne(SQL_SELECT_BY_ID, id);
        if (optMpaRating.isEmpty()) {
            log.info("МПА рейтинг с ID {} в БД не найден", id);
        } else {
            log.info("МПА рейтинг с ID {} в БД найден: {}", id, optMpaRating.get());
        }
        return optMpaRating;
    }
}
