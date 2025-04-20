package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Primary
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    @Override
    public Collection<User> getAll() {
        log.debug("Получено {} пользователей", users.size());
        return users.values();
    }

    @Override
    public User add(User user) {
        normalizeUser(user);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь с ID {}: {}. Всего пользователей: {}", user.getId(), user, users.size());
        return user;
    }

    @Override
    public User update(User user) {
        normalizeUser(user);
        users.put(user.getId(), user);
        log.debug("Обновлён пользователь с ID {}: {}", user.getId(), user);
        return user;
    }

    @Override
    public User getById(Long id) {
        User user = users.get(id);
        if (user == null) {
            log.debug("Пользователь с ID {} не найден", id);
        } else {
            log.trace("Пользователь с ID {} найден: {}", id, user);
        }
        return user;
    }

    private void normalizeUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя пустое. Устанавливаем login '{}' в качестве имени", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}