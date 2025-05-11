package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FriendshipAddResult;
import ru.yandex.practicum.filmorate.model.enums.FriendshipRemoveResult;
import ru.yandex.practicum.filmorate.model.enums.FriendshipStatusCode;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    @Override
    public Collection<User> getAll() {
        log.info("Получено {} пользователей", users.size());
        return users.values();
    }

    @Override
    public User add(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь с ID {}: {}. Всего пользователей: {}", user.getId(), user, users.size());
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.info("Обновлён пользователь с ID {}: {}", user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getById(Long id) {
        Optional<User> optUser = Optional.ofNullable(users.get(id));
        optUser.ifPresentOrElse(
                (user) -> log.info("Пользователь с ID {} найден: {}", id, user),
                () -> log.info("Пользователь с ID {} не найден", id)
        );
        return optUser;
    }

    @Override
    public FriendshipAddResult addFriend(Long userId, Long friendId) {
        Optional<User> optUser = getById(userId);
        if (optUser.isEmpty()) return FriendshipAddResult.UNKNOWN;

        Optional<User> optFriend = getById(friendId);
        if (optFriend.isEmpty()) return FriendshipAddResult.UNKNOWN;

        User user = optUser.get();
        User friend = optFriend.get();
        Map<Long, FriendshipStatus> userFriends = user.getFriends();
        Map<Long, FriendshipStatus> friendFriends = friend.getFriends();

        // Проверка наличия заявки со стороны юзера
        FriendshipStatus friendshipStatusOfUser = userFriends.get(friendId);
        if (friendshipStatusOfUser != null) {
            String friendshipStatusOfUserCode = friendshipStatusOfUser.getCode();
            if (Objects.equals(friendshipStatusOfUserCode, FriendshipStatusCode.CONFIRMED.name())) {
                return FriendshipAddResult.FRIENDSHIP_ALREADY_EXISTS;
            } else if (Objects.equals(friendshipStatusOfUserCode, FriendshipStatusCode.PENDING.name())) {
                return FriendshipAddResult.FRIEND_REQUEST_ALREADY_EXISTS;
            } else {
                return FriendshipAddResult.UNKNOWN; // Нездоровая ситуация, статус был не из числа известных
            }
        }

        // Проверка на встречную заявку
        FriendshipStatus userFriendshipStatus = new FriendshipStatus();
        FriendshipStatus friendshipStatusOfFriend = friendFriends.get(userId);
        if (friendshipStatusOfFriend != null) {
            if (Objects.equals(friendshipStatusOfFriend.getCode(), FriendshipStatusCode.PENDING.name())) {
                // Простановка статуса дружбы юзеру
                userFriendshipStatus.setCode(FriendshipStatusCode.CONFIRMED.name());
                userFriends.put(friendId, userFriendshipStatus);

                // Простановка статуса дружбы другу
                friendshipStatusOfFriend.setCode(FriendshipStatusCode.CONFIRMED.name());

                return FriendshipAddResult.FRIENDSHIP_CONFIRMED;
            }
        }

        // Простановка статуса дружбы юзеру
        userFriendshipStatus.setCode(FriendshipStatusCode.PENDING.name());
        userFriends.put(friendId, userFriendshipStatus);

        return FriendshipAddResult.FRIEND_REQUEST_ADDED;
    }

    @Override
    public FriendshipRemoveResult removeFriend(Long userId, Long friendId) {
        Optional<User> optUser = getById(userId);
        if (optUser.isEmpty()) return FriendshipRemoveResult.UNKNOWN;

        Optional<User> optFriend = getById(friendId);
        if (optFriend.isEmpty()) return FriendshipRemoveResult.UNKNOWN;

        User user = optUser.get();
        User friend = optFriend.get();
        Map<Long, FriendshipStatus> userFriends = user.getFriends();
        Map<Long, FriendshipStatus> friendFriends = friend.getFriends();

        FriendshipStatus friendshipStatusOfUser = userFriends.get(friendId);
        if (friendshipStatusOfUser == null) {
            return FriendshipRemoveResult.NO_FRIENDSHIP;
        }
        String friendshipStatusOfUserCode = friendshipStatusOfUser.getCode();

        // Удаляем информацию о дружбе для юзера
        userFriends.remove(friendId);

        if (Objects.equals(friendshipStatusOfUserCode, FriendshipStatusCode.CONFIRMED.name())) {
            // Откат второй стороны на PENDING
            FriendshipStatus friendshipStatusOfFriend = friendFriends.get(userId);
            friendshipStatusOfFriend.setCode(FriendshipStatusCode.PENDING.name());
            return FriendshipRemoveResult.CONFIRMED_FRIENDSHIP_REMOVED;
        } else if (Objects.equals(friendshipStatusOfUserCode, FriendshipStatusCode.PENDING.name())) {
            return FriendshipRemoveResult.FRIEND_REQUEST_REMOVED;
        } else {
            return FriendshipRemoveResult.UNKNOWN; // Нездоровая ситуация, статус был не из числа известных
        }
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        Optional<User> optUser = getById(userId);
        if (optUser.isEmpty()) return List.of();
        User user = optUser.get();

        return user.getFriends().keySet().stream()
                .map(friendId -> getById(friendId).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        Optional<User> optUser = getById(userId);
        if (optUser.isEmpty()) return null;
        User user = optUser.get();

        Optional<User> optOtherUser = getById(otherUserId);
        if (optOtherUser.isEmpty()) return null;
        User otherUser = optOtherUser.get();

        Set<Long> userFriendsIds = user.getFriends().keySet();
        Set<Long> otherUserFriendsIds = otherUser.getFriends().keySet();
        log.info("Количество друзей пользователя {}: {}", userId, userFriendsIds.size());
        log.info("Количество друзей другого пользователя {}: {}", userId, otherUserFriendsIds.size());

        Set<Long> commonIds = new HashSet<>(userFriendsIds);
        if (!commonIds.retainAll(otherUserFriendsIds)) {
            return List.of();
        }

        return commonIds.stream()
                .map(commonFriendId -> getById(commonFriendId).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void removeUser(Long id) {
        log.info("Удаление пользователя с ID {}", id);
        if (id == null || !users.containsKey(id)) {
            log.warn("Пользователь с ID {} не найден", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }

        for (User user : users.values()) {
            user.getFriends().remove(id);
        }

        users.remove(id);
        log.info("Пользователь с ID {} удален", id);
    }
}