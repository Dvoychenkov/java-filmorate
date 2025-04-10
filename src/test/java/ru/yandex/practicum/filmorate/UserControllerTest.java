package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private UserController controller;

    @BeforeEach
    void contextLoads() {
        controller = new UserController();
    }

    @Test
    void shouldThrowWhenEmailIsIncorrect() {
        User user = new User();
        user.setEmail("  ");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ValidationException ex = assertThrows(ValidationException.class, () -> controller.create(user));
        assertEquals("Пустой e-mail пользователя", ex.getMessage());

        user.setEmail(null);
        ex = assertThrows(ValidationException.class, () -> controller.create(user));
        assertEquals("Пустой e-mail пользователя", ex.getMessage());

        user.setEmail("ema.il");
        ex = assertThrows(ValidationException.class, () -> controller.create(user));
        assertEquals("Некорректный e-mail пользователя", ex.getMessage());

        user.setEmail("e@ma.il");
        assertDoesNotThrow(() -> controller.create(user), "Валидация должна проходить успешно");
    }

    @Test
    void shouldThrowWhenLoginIsIncorrect() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setLogin("bad login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ValidationException ex = assertThrows(ValidationException.class, () -> controller.create(user));
        assertEquals("Логин пользователя содержит пробелы", ex.getMessage());

        user.setLogin(null);
        ex = assertThrows(ValidationException.class, () -> controller.create(user));
        assertEquals("Пустой логин пользователя", ex.getMessage());

        user.setLogin(" ");
        ex = assertThrows(ValidationException.class, () -> controller.create(user));
        assertEquals("Пустой логин пользователя", ex.getMessage());

        user.setLogin("okLogin");
        assertDoesNotThrow(() -> controller.create(user), "Валидация должна проходить успешно");
    }

    @Test
    void shouldReplaceBlankNameWithLogin() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setLogin("login");
        user.setName("  ");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = controller.create(user);
        assertEquals("login", createdUser.getName());

        user.setName(null);
        createdUser = controller.create(user);
        assertEquals("login", createdUser.getName());
    }

    @Test
    void shouldThrowWhenBirthdayIsIncorrect() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException ex = assertThrows(ValidationException.class, () -> controller.create(user));
        assertEquals("Дата рождения пользователя больше текущей даты", ex.getMessage());

        user.setBirthday(null);
        ex = assertThrows(ValidationException.class, () -> controller.create(user));
        assertEquals("Пустая дата рождения пользователя", ex.getMessage());
    }

    @Test
    void shouldThrowWhenUserIsNull() {
        assertThrows(NullPointerException.class, () -> controller.create(null));
    }

    @Test
    void shouldThrowWhenUserIsEmpty() {
        User user = new User();
        assertThrows(ValidationException.class, () -> controller.create(user));
    }
}
