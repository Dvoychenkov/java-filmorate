package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;
    private static final String REGEX_SPACES = ".*\\s+.*";

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validateForCreate(user);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null || !users.containsKey(newUser.getId())) {
            throw new ValidationException("Пользователь с указанным ID не найден");
        }

        User existingUser = users.get(newUser.getId());
        validateForUpdate(newUser, existingUser);
        return existingUser;
    }

    private void validateForCreate(User user) {
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
            user.setName(user.getLogin());
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(Instant.now())) {
            throw new ValidationException("Дата рождения пользователя больше текущей даты");
        }
    }

    private void validateForUpdate(User newUser, User existingUser) {
        if (newUser.getEmail() != null) {
            if (newUser.getEmail().isBlank()) {
                throw new ValidationException("Пустой e-mail пользователя");
            }
            if (!newUser.getEmail().contains("@")) {
                throw new ValidationException("Некорректный e-mail пользователя");
            }
            existingUser.setEmail(newUser.getEmail());
        }

        if (newUser.getLogin() != null) {
            if (newUser.getLogin().isBlank()) {
                throw new ValidationException("Пустой логин пользователя");
            }
            if (newUser.getLogin().matches(REGEX_SPACES)) {
                throw new ValidationException("Логин пользователя содержит пробелы");
            }
            existingUser.setLogin(newUser.getLogin());
        }

        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            existingUser.setName(newUser.getName());
        }

        if (newUser.getBirthday() != null) {
            if (newUser.getBirthday().isAfter(Instant.now())) {
                throw new ValidationException("Дата рождения пользователя больше текущей даты");
            }
            existingUser.setBirthday(newUser.getBirthday());
        }
    }
}
