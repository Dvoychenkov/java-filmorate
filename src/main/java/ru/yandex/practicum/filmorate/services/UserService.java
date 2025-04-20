package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        // Проверка на наличие пользователя
        getUser(user.getId());

        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        Set<Long> userFriendsIds = user.getFriendsIds();
        Set<Long> friendFriendsIds = friend.getFriendsIds();

        userFriendsIds.add(friendId);
        friendFriendsIds.add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        Set<Long> userFriendsIds = user.getFriendsIds();
        Set<Long> friendFriendsIds = friend.getFriendsIds();

        userFriendsIds.remove(friendId);
        friendFriendsIds.remove(userId);
    }

    public List<User> getFriends(Long userId) {
        User user = getUser(userId);
        return user.getFriendsIds().stream()
                .map(userStorage::getById)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = getUser(userId);
        User otherUser = getUser(otherUserId);

        Set<Long> userFriendsIds = user.getFriendsIds();
        Set<Long> otherUserFriendsIds = otherUser.getFriendsIds();

        Set<Long> commonIds = new HashSet<>(userFriendsIds);
        boolean hasCommon = commonIds.retainAll(otherUserFriendsIds);
        if (!hasCommon) {
            return new ArrayList<>();
        }

        return commonIds.stream()
                .map(userStorage::getById)
                .filter(Objects::nonNull)
                .toList();
    }

    public User getUser(Long id) {
        if (id == null) {
            throw new ValidationException("Некорректный ID пользователя");
        }

        User user = userStorage.getById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }
}