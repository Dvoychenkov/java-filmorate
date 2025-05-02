package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FriendshipAddResult;
import ru.yandex.practicum.filmorate.model.enums.FriendshipRemoveResult;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserRowMapper;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.yandex.practicum.filmorate.util.TestHelper.generateUser;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void shouldReturnAllUsers() {
        Collection<User> initUsers = userStorage.getAll();
        assertThat(initUsers).isNotNull();
        assertThat(initUsers).isEmpty();

        int addedCnt = 10;
        for (long i = 1; i <= addedCnt; i++) {
            userStorage.add(generateUser());
        }

        Collection<User> allUsers = userStorage.getAll();
        assertThat(allUsers).isNotNull();
        assertThat(allUsers).isNotEmpty();
        assertThat(allUsers).hasSize(addedCnt);
    }

    @Test
    void shouldAddAndGetUser() {
        User userToCreate = generateUser();
        User createdUser = userStorage.add(userToCreate);
        Optional<User> createdUserFromDB = userStorage.getById(createdUser.getId());

        assertThat(createdUserFromDB)
                .isPresent()
                .contains(createdUser);
    }

    @Test
    void shouldUpdateUser() {
        User userToCreate = generateUser();
        User createdUser = userStorage.add(userToCreate);

        User userToUpdate = generateUser();
        userToUpdate.setId(createdUser.getId());
        User updatedUser = userStorage.update(userToUpdate);

        Optional<User> updatedUserFromDB = userStorage.getById(updatedUser.getId());
        assertThat(updatedUserFromDB)
                .isPresent()
                .contains(userToUpdate);
    }

    @Test
    void shouldAddFriends() {
        User user1 = userStorage.add(generateUser());
        User user2 = userStorage.add(generateUser());

        // Первая заявка от user1 к user2: PENDING
        FriendshipAddResult result1 = userStorage.addFriend(user1.getId(), user2.getId());
        assertThat(result1).isEqualTo(FriendshipAddResult.FRIEND_REQUEST_ADDED);

        // Повторная заявка от user1: уже PENDING
        FriendshipAddResult result2 = userStorage.addFriend(user1.getId(), user2.getId());
        assertThat(result2).isEqualTo(FriendshipAddResult.FRIEND_REQUEST_ALREADY_EXISTS);

        // Обратная заявка от user2 к user1: CONFIRMED
        FriendshipAddResult result3 = userStorage.addFriend(user2.getId(), user1.getId());
        assertThat(result3).isEqualTo(FriendshipAddResult.FRIENDSHIP_CONFIRMED);

        // Повторная заявка от user2: уже CONFIRMED
        FriendshipAddResult result4 = userStorage.addFriend(user2.getId(), user1.getId());
        assertThat(result4).isEqualTo(FriendshipAddResult.FRIENDSHIP_ALREADY_EXISTS);
    }

    @Test
    void shouldRemoveFriends() {
        User u1 = userStorage.add(generateUser());
        User u2 = userStorage.add(generateUser());

        // Взаимно добавились: CONFIRMED у обоих сторон
        userStorage.addFriend(u1.getId(), u2.getId());
        userStorage.addFriend(u2.getId(), u1.getId());

        // Удаление: CONFIRMED удаляется для того, кто удалил друга, обратная связь становится PENDING
        FriendshipRemoveResult removed1 = userStorage.removeFriend(u1.getId(), u2.getId());
        assertThat(removed1).isEqualTo(FriendshipRemoveResult.CONFIRMED_FRIENDSHIP_REMOVED);

        // Повторное удаление: обратная связь уже PENDING
        FriendshipRemoveResult removed2 = userStorage.removeFriend(u2.getId(), u1.getId());
        assertThat(removed2).isEqualTo(FriendshipRemoveResult.FRIEND_REQUEST_REMOVED);

        // Отмена заявки с другой стороны
        FriendshipRemoveResult removed3 = userStorage.removeFriend(u1.getId(), u2.getId());
        assertThat(removed3).isEqualTo(FriendshipRemoveResult.NO_FRIENDSHIP);
    }

    @Test
    void shouldReturnFriends() {
        User u1 = userStorage.add(generateUser());
        User u2 = userStorage.add(generateUser());
        User u3 = userStorage.add(generateUser());
        User u4 = userStorage.add(generateUser());

        userStorage.addFriend(u1.getId(), u2.getId());
        userStorage.addFriend(u1.getId(), u3.getId());
        userStorage.addFriend(u1.getId(), u4.getId());

        Collection<User> friends = userStorage.getFriends(u1.getId());
        assertThat(friends)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(u2.getId(), u3.getId(), u4.getId());
    }

    @Test
    void shouldReturnCommonFriends() {
        User u1 = userStorage.add(generateUser());
        User u2 = userStorage.add(generateUser());
        User u3 = userStorage.add(generateUser());

        userStorage.addFriend(u1.getId(), u3.getId());
        userStorage.addFriend(u2.getId(), u3.getId());

        Collection<User> commonFriend = userStorage.getCommonFriends(u1.getId(), u2.getId());
        assertThat(commonFriend)
                .extracting(User::getId)
                .containsExactly(u3.getId());
    }
}
