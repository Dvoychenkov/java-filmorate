package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;
    private static final String REGEX_SPACES = ".*\\s+.*";

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validate(user);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с указанным ID не найден");
        }
        validate(user);
        users.put(user.getId(), user);
        log.info("Обновлён пользователь: {}", user);
        return user;
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Пустой e-mail пользователя");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный e-mail пользователя");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Пустой логин пользователя");
        }
        if (user.getLogin().matches(REGEX_SPACES)) {
            throw new ValidationException("Логин пользователя содержит пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя пользователя пустое, за основу взят логин");
            user.setName(user.getLogin());
        }

        if (user.getBirthday() == null) {
            throw new ValidationException("Пустая дата рождения пользователя");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения пользователя больше текущей даты");
        }
    }
}
