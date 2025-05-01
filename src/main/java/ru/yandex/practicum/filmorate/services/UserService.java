package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FriendshipAddResult;
import ru.yandex.practicum.filmorate.model.enums.FriendshipRemoveResult;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.validation.ValidationUtils.requireFound;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getAll() {
        Collection<User> users = userStorage.getAll();
        log.info("Получено {} пользователей", users.size());
        return users;
    }

    public User create(User user) {
        User created = userStorage.add(user);
        if (created == null) throw new IllegalStateException("Не удалось сохранить данные для нового пользователя");
        log.info("Создан пользователь: {}", created);
        return created;
    }

    public User update(User user) {
        getUser(user.getId()); // Проверка на наличие пользователя

        User updated = userStorage.update(user);
        log.info("Обновлён пользователь: {}", updated);
        return updated;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUser(userId); // Проверка на наличие пользователя
        User friend = getUser(friendId); // Проверка на наличие друга

        FriendshipAddResult friendshipAddResult = userStorage.addFriend(userId, friendId);
        switch (friendshipAddResult) {
            case FRIEND_REQUEST_ADDED -> log.info("Отправлена новая заявка в друзья от {} к {}", userId, friendId);
            case FRIEND_REQUEST_ALREADY_EXISTS -> log.info("Заявка в друзья уже была от {} к {}", userId, friendId);
            case FRIENDSHIP_CONFIRMED -> log.info("Дружба подтверждена между {} и {}", userId, friendId);
            case FRIENDSHIP_ALREADY_EXISTS -> log.info("Дружба уже существует между {} и {}", userId, friendId);
            case UNKNOWN -> log.info("Неизвестное состояние создания дружбы между {} и {}", userId, friendId);
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUser(userId); // Проверка на наличие пользователя
        User friend = getUser(friendId); // Проверка на наличие друга

        FriendshipRemoveResult friendshipRemoveResult = userStorage.removeFriend(userId, friendId);
        switch (friendshipRemoveResult) {
            case CONFIRMED_FRIENDSHIP_REMOVED -> log.info("Дружба отменена от {} к {}", userId, friendId);
            case FRIEND_REQUEST_REMOVED -> log.info("Заявка отменена от {} к {}", userId, friendId);
            case NO_FRIENDSHIP -> log.info("Дружбы/заявки не было от {} к {}", userId, friendId);
            case UNKNOWN -> log.info("Неизвестное состояние разрыва дружбы между {} и {}", userId, friendId);
        }
    }

    public Collection<User> getFriends(Long userId) {
        User user = getUser(userId); // Проверка на наличие пользователя

        Collection<User> friends = userStorage.getFriends(userId);
        log.info("У пользователя {} найдено {} друзей", userId, friends.size());
        return friends;
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = getUser(userId); // Проверка на наличие пользователя
        User otherUser = getUser(otherUserId); // Проверка на наличие другого пользователя

        Collection<User> commonFriends = userStorage.getCommonFriends(userId, otherUserId);
        if (commonFriends.isEmpty()) {
            log.info("У пользователей нет общих друзей: {} и {}", userId, otherUserId);
        } else {
            log.info("Количество общих друзей для пользователей {} и {}: {}", userId, otherUserId, commonFriends.size());
        }
        return commonFriends;
    }

    public User getUser(Long id) {
        if (id == null) throw new ValidationException("Некорректный ID пользователя");
        User user = requireFound(userStorage.getById(id), () -> "Пользователь с ID " + id + " не найден");
        log.info("Получен пользователь по ID {}: {}", id, user);
        return user;
    }
}