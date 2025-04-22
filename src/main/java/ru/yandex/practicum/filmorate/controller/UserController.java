package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.addFriend(userId, friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.removeFriend(userId, friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public Collection<User> getFriends(@PathVariable Long userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public Collection<User> getCommonFriends(@PathVariable Long userId, @PathVariable Long otherUserId) {
        return userService.getCommonFriends(userId, otherUserId);
    }
}