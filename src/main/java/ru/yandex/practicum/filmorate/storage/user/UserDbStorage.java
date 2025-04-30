package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.base.BaseCRUDRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("userDbStorage")
@Slf4j
public class UserDbStorage extends BaseCRUDRepository<User> implements UserStorage {
    private static final String SQL_SELECT_ALL = "SELECT * FROM users";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String SQL_INSERT_USER = """
            INSERT INTO users (email, login, name, birthday)
            VALUES (?, ?, ?, ?)
            """;
    private static final String SQL_UPDATE_USER = """
            UPDATE users
            SET email = ?, login = ?, name = ?, birthday = ?
            WHERE id = ?
            """;

    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        super(jdbcTemplate, userRowMapper);
    }

    @Override
    public Collection<User> getAll() {
        Collection<User> users = queryMany(SQL_SELECT_ALL);
        log.info("Получено {} пользователей из БД", users.size());
        return users;
    }

    @Override
    public User add(User user) {
        Long id = insertAndReturnId(SQL_INSERT_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        log.info("Пользователь добавлен в БД: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        update(SQL_UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        log.info("Пользователь обновлён в БД: {}", user);
        return user;
    }

    @Override
    public User getById(Long id) {
        Optional<User> optUser = queryOne(SQL_SELECT_BY_ID, id);
        if (optUser.isEmpty()) {
            log.info("Пользователь с ID {} в БД не найден", id);
            return null;
        } else {
            User user = optUser.get();
            log.info("Пользователь с ID {} в БД найден: {}", id, user);
            return user;
        }
    }
}
