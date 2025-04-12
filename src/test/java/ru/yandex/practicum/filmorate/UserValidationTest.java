package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailIncorrectEmail() {
        User user = new User();
        user.setEmail("ema.il");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldFailEmptyEmail() {
        User user = new User();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));

        user.setEmail("");
        violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));

        user.setEmail(" ");
        violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldSuccessCorrectEmail() {
        User user = new User();
        user.setEmail("e@ma.il");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldFailIncorrectLogin() {
        User user = new User();
        user.setLogin("bad login");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldFailEmptyLogin() {
        User user = new User();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));

        user.setLogin("");
        violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));

        user.setLogin(" ");
        violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldSuccessCorrectLogin() {
        User user = new User();
        user.setLogin("okLogin");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldSuccessEmptyName() {
        User user = new User();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));

        user.setName("");
        violations = validator.validate(user);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));

        user.setName(" ");
        violations = validator.validate(user);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldNormalizeEmptyName() {
        User user = new User();
        user.setLogin("okLogin");

        UserController controller = new UserController();
        User createdUser = controller.create(user);
        assertEquals("okLogin", createdUser.getName());

        user.setName("");
        createdUser = controller.create(user);
        assertEquals("okLogin", createdUser.getName());

        user.setName(" ");
        createdUser = controller.create(user);
        assertEquals("okLogin", createdUser.getName());
    }

    @Test
    void shouldSuccessCorrectName() {
        User user = new User();
        user.setName("Семён Семёныч");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldFailIncorrectBirthday() {
        User user = new User();
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));

        user.setBirthday(LocalDate.now().plusDays(99));
        violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }

    @Test
    void shouldFailEmptyBirthday() {
        User user = new User();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }

    @Test
    void shouldSuccessCorrectBirthday() {
        User user = new User();
        user.setBirthday(LocalDate.now().minusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }

    @Test
    void shouldSuccessCorrectUser() {
        User user = new User();
        user.setEmail("e@ma.il");
        user.setLogin("okLogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());

        user.setName("Семён Семёныч");
        violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailEmptyUser() {
        User user = new User();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        User emptyUser = null;
        assertThrows(IllegalArgumentException.class, () -> validator.validate(emptyUser));
    }
}