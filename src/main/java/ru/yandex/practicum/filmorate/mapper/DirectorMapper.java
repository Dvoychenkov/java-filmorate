package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;

@Component
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
