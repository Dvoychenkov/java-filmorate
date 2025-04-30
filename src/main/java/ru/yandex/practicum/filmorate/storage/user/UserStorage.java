package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAll();

    // TODO рассмотреть вариант смены на Optional<User> (правка реализаций + сервиса)
    User getById(Long id);

    User add(User user);

    User update(User user);
}