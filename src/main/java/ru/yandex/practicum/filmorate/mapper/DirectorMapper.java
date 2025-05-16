package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Director;

@Component
@RequiredArgsConstructor
public class DirectorMapper {
    public Director mapToDirector(NewDirectorRequest request) {
        Director director = new Director();
        director.setName(request.getName());
        return director;
    }

    public DirectorDto mapToDirectorDto(Director director) {
        return new DirectorDto(
                director.getId(),
                director.getName()
        );
    }

    public void updateDirectorFromRequest(Director director, UpdateDirectorRequest request) {
        director.setName(request.getName());
    }
}
