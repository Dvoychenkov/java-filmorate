package ru.yandex.practicum.filmorate.dto;

import lombok.Value;

import java.time.LocalDate;

@Value
public class UserDto {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}