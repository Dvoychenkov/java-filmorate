package ru.yandex.practicum.filmorate.storage.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;

@Slf4j
public abstract class BaseCRUDRepository<T> extends BaseReadRepository<T> {
    public BaseCRUDRepository(JdbcTemplate jdbcTemplate, RowMapper<T> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    protected boolean delete(String sql, Object... args) {
        int rowsDeleted = jdbcTemplate.update(sql, args);
        return rowsDeleted > 0;
    }

    protected void update(String sql, Object... args) {
        int rowsUpdated = jdbcTemplate.update(sql, args);
        if (rowsUpdated == 0) {
            throw new NotFoundException("Не удалось найти данные для обновления, args: " + Arrays.toString(args));
        }
    }

    protected Long insertAndReturnId(String sql, Object... args) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            throw new IllegalStateException("Не удалось сохранить данные");
        }
    }
}
