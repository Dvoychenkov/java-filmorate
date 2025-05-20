package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.services.FeedService;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.validation.IdValid;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FeedService feedService;

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getById(@IdValid("userId") @PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody NewUserRequest user) {
        UserDto created = userService.create(user);
        log.info("Создан пользователь: {}", created);
        return created;
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserRequest user) {
        UserDto updated = userService.update(user);
        log.info("Обновлён пользователь: {}", updated);
        return updated;
    }

    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void addFriend(
            @IdValid("userId") @PathVariable Long userId,
            @IdValid("friendId") @PathVariable Long friendId
    ) {
        userService.addFriend(userId, friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void removeFriend(
            @IdValid("userId") @PathVariable Long userId,
            @IdValid("friendId") @PathVariable Long friendId
    ) {
        userService.removeFriend(userId, friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public Collection<UserDto> getFriends(@IdValid("userId") @PathVariable Long userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public Collection<UserDto> getCommonFriends(
            @IdValid("userId") @PathVariable Long userId,
            @IdValid("friendId") @PathVariable Long otherUserId
    ) {
        return userService.getCommonFriends(userId, otherUserId);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@IdValid("userId") @PathVariable Long userId) {
        userService.removeUser(userId);
        log.info("Пользователь с ID {} удалён", userId);
    }

    @GetMapping("/{userId}/feed")
    public Collection<FeedEvent> getUserFeed(@IdValid("userId") @PathVariable Long userId) {
        userService.getUserOrThrow(userId); // Проверка на наличие пользователя
        return feedService.getFeedByUserId(userId);
    }

    @GetMapping("/{userId}/recommendations")
    public Collection<FilmDto> getRecommendations(@IdValid("userId") @PathVariable Long userId) {
        return userService.getFilmsRecommendations(userId);
    }
}