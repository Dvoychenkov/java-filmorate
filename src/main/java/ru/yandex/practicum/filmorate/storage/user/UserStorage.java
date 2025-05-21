package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FriendshipAddResult;
import ru.yandex.practicum.filmorate.model.enums.FriendshipRemoveResult;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getAll();

    Optional<User> getById(Long id);

    User add(User user);

    User update(User user);

    FriendshipAddResult addFriend(Long userId, Long friendId);

    FriendshipRemoveResult removeFriend(Long userId, Long friendId);

    Collection<User> getFriends(Long userId);

    Collection<User> getCommonFriends(Long userId, Long otherUserId);

    void removeUser(Long id);
}