package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorService {
    Collection<DirectorDto> getAll();

    DirectorDto create(NewDirectorRequest newRequestDirector);

    void delete(Long id);

    DirectorDto update(UpdateDirectorRequest updateRequestDirector);

    DirectorDto getDirector(Long id);

    Director getDirectorOrThrow(Long id);
}
