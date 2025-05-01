package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.services.MpaRatingService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaRatingService mpaRatingService;

    @GetMapping
    public Collection<MpaRatingDto> getAll() {
        return mpaRatingService.getAll();
    }

    @GetMapping("/{id}")
    public MpaRatingDto getById(@PathVariable Long id) {
        return mpaRatingService.getMpaRating(id);
    }
}