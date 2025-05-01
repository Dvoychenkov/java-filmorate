package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    @Override
    public Collection<User> getAll() {
        log.info("Получено {} пользователей", users.size());
        return users.values();
    }

    @Override
    public User add(User user) {
        normalizeUser(user);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь с ID {}: {}. Всего пользователей: {}", user.getId(), user, users.size());
        return user;
    }

    @Override
    public User update(User user) {
        normalizeUser(user);
        users.put(user.getId(), user);
        log.info("Обновлён пользователь с ID {}: {}", user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getById(Long id) {
        Optional<User> optUser = Optional.ofNullable(users.get(id));
        optUser.ifPresentOrElse(
                (user) -> log.info("Пользователь с ID {} найден: {}", id, user),
                () -> log.info("Пользователь с ID {} не найден", id)
        );
        return optUser;
    }

    // TODO - по хорошему перенести нормалайзер на уровень сервиса в рамках DTO сущности - UserMapper.mapToUser(request)
    private void normalizeUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя пустое. Устанавливаем login '{}' в качестве имени", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}