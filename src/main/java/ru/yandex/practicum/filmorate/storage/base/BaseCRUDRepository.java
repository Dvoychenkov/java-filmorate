package ru.yandex.practicum.filmorate.storage.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Slf4j
public abstract class BaseCRUDRepository<T> extends BaseReadRepository<T> {
    public BaseCRUDRepository(JdbcTemplate jdbcTemplate, RowMapper<T> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    protected int delete(String sql, Object... args) {
        return jdbcTemplate.update(sql, args);
    }

    protected int update(String sql, Object... args) {
        return jdbcTemplate.update(sql, args);
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

        return keyHolder.getKeyAs(Long.class);
    }
}
