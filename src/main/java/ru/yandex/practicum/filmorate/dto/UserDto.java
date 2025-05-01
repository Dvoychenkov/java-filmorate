package ru.yandex.practicum.filmorate.dto;

import lombok.Value;

import java.time.LocalDate;

@Value
public class UserDto {
    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
}