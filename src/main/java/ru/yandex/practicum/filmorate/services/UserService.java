package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;
import ru.yandex.practicum.filmorate.model.enums.FriendshipAddResult;
import ru.yandex.practicum.filmorate.model.enums.FriendshipRemoveResult;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.ValidationUtils.requireFound;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final UserMapper userMapper;
    private final FilmMapper filmMapper;
    private final FeedService feedService;

    public Collection<UserDto> getAll() {
        Collection<User> users = userStorage.getAll();
        log.info("Получено {} пользователей", users.size());
        return users.stream()
                .map(userMapper::mapToUserDto)
                .toList();
    }

    public UserDto create(NewUserRequest newRequestUser) {
        User createdUser = userStorage.add(userMapper.mapToUser(newRequestUser));
        log.info("Создан пользователь: {}", createdUser);
        return userMapper.mapToUserDto(createdUser);
    }

    public UserDto update(UpdateUserRequest updateRequestUser) {
        User userToUpdate = getUserOrThrow(updateRequestUser.getId());
        userMapper.updateUserFromRequest(userToUpdate, updateRequestUser);
        User updatedUser = userStorage.update(userToUpdate);
        log.info("Обновлён пользователь: {}", updatedUser);
        return userMapper.mapToUserDto(updatedUser);
    }

    public void addFriend(Long userId, Long friendId) {
        getUserOrThrow(userId); // Проверка на наличие пользователя
        getUserOrThrow(friendId); // Проверка на наличие друга

        FriendshipAddResult friendshipAddResult = userStorage.addFriend(userId, friendId);
        switch (friendshipAddResult) {
            case FRIEND_REQUEST_ADDED -> {
                feedService.addEvent(new FeedEvent(userId, FeedEventType.FRIEND, FeedOperation.ADD, friendId));
                log.info("Отправлена новая заявка в друзья от {} к {}", userId, friendId);
            }
            case FRIEND_REQUEST_ALREADY_EXISTS -> log.info("Заявка в друзья уже была от {} к {}", userId, friendId);
            case FRIENDSHIP_CONFIRMED -> {
                feedService.addEvent(new FeedEvent(userId, FeedEventType.FRIEND, FeedOperation.ADD, friendId));
                log.info("Дружба подтверждена между {} и {}", userId, friendId);
            }
            case FRIENDSHIP_ALREADY_EXISTS -> log.info("Дружба уже существует между {} и {}", userId, friendId);
            case UNKNOWN -> log.info("Неизвестное состояние создания дружбы между {} и {}", userId, friendId);
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        getUserOrThrow(userId); // Проверка на наличие пользователя
        getUserOrThrow(friendId); // Проверка на наличие друга

        FriendshipRemoveResult friendshipRemoveResult = userStorage.removeFriend(userId, friendId);
        switch (friendshipRemoveResult) {
            case CONFIRMED_FRIENDSHIP_REMOVED -> {
                feedService.addEvent(new FeedEvent(userId, FeedEventType.FRIEND, FeedOperation.REMOVE, friendId));
                log.info("Дружба отменена от {} к {}", userId, friendId);
            }
            case FRIEND_REQUEST_REMOVED -> {
                feedService.addEvent(new FeedEvent(userId, FeedEventType.FRIEND, FeedOperation.REMOVE, friendId));
                log.info("Заявка отменена от {} к {}", userId, friendId);
            }
            case NO_FRIENDSHIP -> log.info("Дружбы/заявки не было от {} к {}", userId, friendId);
            case UNKNOWN -> log.info("Неизвестное состояние разрыва дружбы между {} и {}", userId, friendId);
        }
    }

    public Collection<UserDto> getFriends(Long userId) {
        getUserOrThrow(userId); // Проверка на наличие пользователя

        Collection<User> friends = userStorage.getFriends(userId);
        log.info("У пользователя {} найдено {} друзей", userId, friends.size());
        return friends.stream()
                .map(userMapper::mapToUserDto)
                .toList();
    }

    public Collection<UserDto> getCommonFriends(Long userId, Long otherUserId) {
        getUserOrThrow(userId); // Проверка на наличие пользователя
        getUserOrThrow(otherUserId); // Проверка на наличие другого пользователя

        Collection<User> commonFriends = userStorage.getCommonFriends(userId, otherUserId);
        if (commonFriends.isEmpty()) {
            log.info("У пользователей нет общих друзей: {} и {}", userId, otherUserId);
        } else {
            log.info("Количество общих друзей для пользователей {} и {}: {}", userId, otherUserId, commonFriends.size());
        }
        return commonFriends.stream()
                .map(userMapper::mapToUserDto)
                .toList();
    }

    public Collection<FilmDto> getFilmsRecommendations(Long userId) {
        getUserOrThrow(userId); // Проверка на наличие пользователя
        Collection<Film> filmsRecommendations = filmStorage.getFilmsRecommendations(userId);
        log.info("Получено {} рекомендаций для пользователя {}", filmsRecommendations.size(), userId);
        return filmsRecommendations.stream()
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    public UserDto getUser(Long id) {
        return userMapper.mapToUserDto(getUserOrThrow(id));
    }

    public User getUserOrThrow(Long id) {
        if (id == null) throw new ValidationException("Некорректный ID пользователя");
        User user = requireFound(userStorage.getById(id), () -> "Пользователь с ID " + id + " не найден");
        log.info("Получен пользователь по ID {}: {}", id, user);
        return user;
    }

    public void removeUser(Long id) {
        userStorage.removeUser(id);
        log.info("Пользователь с ID {} удалён", id);
    }
}