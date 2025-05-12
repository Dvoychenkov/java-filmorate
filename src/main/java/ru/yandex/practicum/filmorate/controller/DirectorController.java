package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.services.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<DirectorDto> getAll() {
        return directorService.getAll();
    }

    @GetMapping("/{directorId}")
    public DirectorDto getById(@PathVariable Long directorId) {
        return directorService.getDirector(directorId);
    }

    @PostMapping
    public DirectorDto create(@Valid @RequestBody NewDirectorRequest director) {
        DirectorDto created = directorService.create(director);
        log.info("Создан режиссер: {}", created);
        return created;
    }

    @DeleteMapping("/{directorId}")
    public void delete(@PathVariable Long directorId) {
        directorService.delete(directorId);
        log.info("Удалён режиссер с id: {}", directorId);
    }

    @PutMapping
    public DirectorDto update(@Valid @RequestBody UpdateDirectorRequest director) {
        DirectorDto updated = directorService.update(director);
        log.info("Обновлён режиссер: {}", updated);
        return updated;
    }
}
