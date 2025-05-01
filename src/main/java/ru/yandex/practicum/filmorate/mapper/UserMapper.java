package ru.yandex.practicum.filmorate.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.UserRequest;
import ru.yandex.practicum.filmorate.model.User;

@Component
@Slf4j
public class UserMapper {
    public User mapToUser(NewUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setLogin(request.getLogin());
        user.setBirthday(request.getBirthday());
        normalizeUser(request, user);
        return user;
    }

    public void updateUserFromRequest(User user, UpdateUserRequest request) {
        user.setEmail(request.getEmail());
        user.setLogin(request.getLogin());
        normalizeUser(request, user);
        user.setBirthday(request.getBirthday());
    }

    public UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
    }

    private static void normalizeUser(UserRequest request, User user) {
        if (request.getName() == null || request.getName().isBlank()) {
            log.info("Имя пользователя пустое. Устанавливаем login '{}' в качестве имени", request.getLogin());
            user.setName(request.getLogin());
        } else {
            user.setName(request.getName());
        }
    }
}