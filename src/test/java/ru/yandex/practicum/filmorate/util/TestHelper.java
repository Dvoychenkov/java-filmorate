package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TestHelper {
    public static User createUser(Long index) {
        int id = index.intValue();
        return new User(
                null,
                "user" + id + "@mail.com",
                "login" + id,
                "Пользователь " + id,
                LocalDate.of(1990 + id, 1, 1),
                new HashMap<>()
        );
    }

    public static Film createFilm(Long index, MpaRating mpaRating, List<Genre> genres) {
        int id = index.intValue();
        return new Film(
                null,
                "Фильм " + id,
                "Описание фильма " + id,
                LocalDate.of(2000 + id, 1, 1),
                100 + id,
                mpaRating,
                new ArrayList<>(genres),
                new HashSet<>()
        );
    }

    public static Film createFilm(Long index, MpaRating mpaRating) {
        return createFilm(index, mpaRating, List.of());
    }
}
