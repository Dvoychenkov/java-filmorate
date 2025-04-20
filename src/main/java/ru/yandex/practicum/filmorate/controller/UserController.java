package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        User created = userService.create(user);
        log.info("Создан пользователь: {}", created);
        return created;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        User updated = userService.update(user);
        log.info("Обновлён пользователь: {}", updated);
        return updated;
    }
}