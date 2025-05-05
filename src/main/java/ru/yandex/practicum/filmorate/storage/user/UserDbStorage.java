package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FriendshipAddResult;
import ru.yandex.practicum.filmorate.model.enums.FriendshipRemoveResult;
import ru.yandex.practicum.filmorate.storage.base.BaseCRUDRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
@Primary
@Slf4j
public class UserDbStorage extends BaseCRUDRepository<User> implements UserStorage {
    // Обработка информации о пользователях
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

    // Обработка информации о добавлении и удалении друзей
    private static final String SQL_CHECK_EXISTING_FRIENDSHIP = """
            SELECT status_id FROM users_friendship
            WHERE user_id = ? AND friend_id = ?
            """;
    private static final String SQL_GET_FRIENDSHIP_STATUS_ID_BY_CODE = "SELECT id FROM friendship_status WHERE code = ?";
    private static final String SQL_GET_FRIENDSHIP_STATUS_CODE_BY_ID = "SELECT code FROM friendship_status WHERE id = ?";

    // Обработка информации о добавлении друзей
    private static final String SQL_INSERT_FRIENDSHIP_PENDING = """
            INSERT INTO users_friendship (user_id, friend_id, status_id)
            VALUES (?, ?, (SELECT id FROM friendship_status WHERE code = 'PENDING'))
            """;
    private static final String SQL_CHECK_REVERSE_PENDING = """
            SELECT COUNT(*) FROM users_friendship
            WHERE user_id = ? AND friend_id = ?
            AND status_id = (SELECT id FROM friendship_status WHERE code = 'PENDING')
            """;
    private static final String SQL_CONFIRM_FRIENDSHIP = """
            UPDATE users_friendship
            SET status_id = (SELECT id FROM friendship_status WHERE code = 'CONFIRMED')
            WHERE (user_id = ? AND friend_id = ?)
            OR (user_id = ? AND friend_id = ?)
            """;

    // Обработка информации об удалении друзей
    private static final String SQL_DELETE_FRIENDSHIP = """
            DELETE FROM users_friendship
            WHERE user_id = ? AND friend_id = ?
            """;
    private static final String SQL_DOWNGRADE_REVERSE_TO_PENDING = """
            UPDATE users_friendship
            SET status_id = (SELECT id FROM friendship_status WHERE code = 'PENDING')
            WHERE user_id = ? AND friend_id = ?
            """;

    // Обработка информации о друзьях и общих друзьях
    // Согласно ТЗ другом юзера считается даже тот юзер, который не совершал ответной заявки в друзья
    private static final String SQL_SELECT_FRIENDS = """
                SELECT u.* FROM users u
                JOIN users_friendship f ON u.id = f.friend_id
                WHERE f.user_id = ?
            """;
    private static final String SQL_SELECT_COMMON_FRIENDS = """
                SELECT u.* FROM users u
                WHERE u.id IN (
                    SELECT f1.friend_id
                    FROM users_friendship f1
                    JOIN users_friendship f2 ON f1.friend_id = f2.friend_id
                    WHERE f1.user_id = ? AND f2.user_id = ?
                )
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

        if (id == null) return null;
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
    public Optional<User> getById(Long id) {
        Optional<User> optUser = queryOne(SQL_SELECT_BY_ID, id);
        optUser.ifPresentOrElse(
                (user) -> log.info("Пользователь с ID {} в БД найден: {}", id, user),
                () -> log.info("Пользователь с ID {} в БД не найден", id)
        );
        return optUser;
    }

    @Override
    public FriendshipAddResult addFriend(Long userId, Long friendId) {
        Integer statusId = jdbcTemplate.query(SQL_CHECK_EXISTING_FRIENDSHIP,
                rs -> rs.next() ? rs.getInt("status_id") : null,
                userId, friendId
        );

        if (statusId != null) {
            // Уже есть связь
            if (statusId == getStatusIdByCode("CONFIRMED")) {
                return FriendshipAddResult.FRIENDSHIP_ALREADY_EXISTS;
            } else if (statusId == getStatusIdByCode("PENDING")) {
                return FriendshipAddResult.FRIEND_REQUEST_ALREADY_EXISTS;
            } else {
                return FriendshipAddResult.UNKNOWN; // Нездоровая ситуация, статус был не из числа известных
            }
        }

        // Создаём заявку
        update(SQL_INSERT_FRIENDSHIP_PENDING, userId, friendId);

        // Проверяем встречную заявку
        Integer reverseExists = jdbcTemplate.queryForObject(SQL_CHECK_REVERSE_PENDING, Integer.class, friendId, userId);
        if (reverseExists != null && reverseExists > 0) {
            // Обе стороны хотят дружить — меняем статусы на подтверждённый
            update(SQL_CONFIRM_FRIENDSHIP, userId, friendId, friendId, userId);
            return FriendshipAddResult.FRIENDSHIP_CONFIRMED;
        }

        return FriendshipAddResult.FRIEND_REQUEST_ADDED;
    }

    @Override
    public FriendshipRemoveResult removeFriend(Long userId, Long friendId) {
        Integer statusId = jdbcTemplate.query(SQL_CHECK_EXISTING_FRIENDSHIP,
                rs -> rs.next() ? rs.getInt("status_id") : null,
                userId, friendId
        );

        if (statusId == null) {
            return FriendshipRemoveResult.NO_FRIENDSHIP;
        }

        // Удаляем информацию о дружбе для юзера
        update(SQL_DELETE_FRIENDSHIP, userId, friendId);

        if (statusId == getStatusIdByCode("CONFIRMED")) {
            // Откат второй стороны на PENDING
            update(SQL_DOWNGRADE_REVERSE_TO_PENDING, friendId, userId);
            return FriendshipRemoveResult.CONFIRMED_FRIENDSHIP_REMOVED;
        } else if (statusId == getStatusIdByCode("PENDING")) {
            return FriendshipRemoveResult.FRIEND_REQUEST_REMOVED;
        } else {
            return FriendshipRemoveResult.UNKNOWN; // Нездоровая ситуация, статус был не из числа известных
        }
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        Collection<User> friends = queryMany(SQL_SELECT_FRIENDS, userId);
        log.info("Получено {} друзей для юзера {} из БД", friends.size(), userId);
        return friends;
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        Collection<User> commonFriends = queryMany(SQL_SELECT_COMMON_FRIENDS, userId, otherUserId);
        log.info("Получено {} общих друзей для юзеров {} и {} из БД", commonFriends.size(), userId, otherUserId);
        return commonFriends;
    }

    private int getStatusIdByCode(String code) {
        return jdbcTemplate.queryForObject(SQL_GET_FRIENDSHIP_STATUS_ID_BY_CODE, Integer.class, code);
    }

    private String getStatusCodeById(Integer id) {
        return jdbcTemplate.queryForObject(SQL_GET_FRIENDSHIP_STATUS_CODE_BY_ID, String.class, id);
    }
}
