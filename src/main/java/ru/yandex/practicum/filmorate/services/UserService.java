package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

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
        log.info("Создан пользователь: {}", created);
        return created;
    }

    public User update(User user) {
        // Проверка на наличие пользователя
        getUser(user.getId());

        User updated = userStorage.update(user);
        log.info("Обновлён пользователь: {}", updated);
        return updated;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        Set<Long> userFriendsIds = user.getFriendsIds();
        Set<Long> friendFriendsIds = friend.getFriendsIds();

        // При добавлении в друзья процесс происходит с обеих сторон автоматически
        boolean added = userFriendsIds.add(friendId);
        if (added) {
            log.info("[Взаимное добавление Пользователь => Друг] Пользователь {} добавил в друзья пользователя {}", userId, friendId);
        } else {
            log.info("[Взаимное добавление Пользователь => Друг] Пользователь {} уже был в друзьях у {}", friendId, userId);
        }

        boolean addedReverse = friendFriendsIds.add(userId);
        if (addedReverse) {
            log.info("[Взаимное добавление Друг => Пользователь] Пользователь {} добавил в друзья пользователя {}", userId, friendId);
        } else {
            log.info("[Взаимное добавление Друг => Пользователь] Пользователь {} уже был в друзьях у {}", friendId, userId);
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        Set<Long> userFriendsIds = user.getFriendsIds();
        Set<Long> friendFriendsIds = friend.getFriendsIds();

        // При удалении из друзей процесс происходит с обеих сторон автоматически
        boolean removed = userFriendsIds.remove(friendId);
        if (removed) {
            log.info("[Взаимное удаление Пользователь => Друг] Пользователь {} удалил из друзей пользователя {}", userId, friendId);
        } else {
            log.info("[Взаимное удаление Пользователь => Друг] Пользователь {} не был в друзьях у {}", friendId, userId);
        }

        boolean removedReverse = friendFriendsIds.remove(userId);
        if (removed) {
            log.info("[Взаимное удаление Друг => Пользователь] Пользователь {} удалил из друзей пользователя {}", userId, friendId);
        } else {
            log.info("[Взаимное удаление Друг => Пользователь] Пользователь {} не был в друзьях у {}", friendId, userId);
        }
    }

    public Collection<User> getFriends(Long userId) {
        User user = getUser(userId);
        Collection<User> friends = user.getFriendsIds().stream()
                .map(userStorage::getById)
                .filter(Objects::nonNull)
                .toList();
        log.info("У пользователя {} найдено {} друзей", userId, friends.size());
        return friends;
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = getUser(userId);
        User otherUser = getUser(otherUserId);

        Set<Long> userFriendsIds = user.getFriendsIds();
        Set<Long> otherUserFriendsIds = otherUser.getFriendsIds();
        log.info("Количество друзей пользователя {}: {}", userId, userFriendsIds.size());
        log.info("Количество друзей другого пользователя {}: {}", userId, otherUserFriendsIds.size());

        Set<Long> commonIds = new HashSet<>(userFriendsIds);
        boolean hasCommon = commonIds.retainAll(otherUserFriendsIds);
        if (!hasCommon) {
            log.info("У пользователей нет общих друзей: {} и {}", userId, otherUserId);
            return new ArrayList<>();
        }

        Collection<User> commonFriends = commonIds.stream()
                .map(userStorage::getById)
                .filter(Objects::nonNull)
                .toList();
        log.info("Количество общих друзей для пользователей {} и {}: {}", userId, otherUserId, commonFriends.size());
        return commonFriends;
    }

    public User getUser(Long id) {
        if (id == null) {
            throw new ValidationException("Некорректный ID пользователя");
        }

        User user = userStorage.getById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        log.info("Получен пользователь по ID {}: {}", id, user);
        return user;
    }
}