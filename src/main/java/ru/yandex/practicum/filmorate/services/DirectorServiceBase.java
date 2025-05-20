package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.ValidationUtils.requireFound;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorServiceBase implements DirectorService {
    private final DirectorStorage directorStorage;
    private final DirectorMapper directorMapper;

    @Override
    public Collection<DirectorDto> getAll() {
        Collection<Director> directors = directorStorage.getAll();
        log.info("Получено {} режиссеров", directors.size());
        return directors.stream()
                .map(directorMapper::mapToDirectorDto)
                .toList();
    }

    @Override
    public DirectorDto create(NewDirectorRequest newRequestDirector) {
        Director createdDirector = directorStorage.add(directorMapper.mapToDirector(newRequestDirector));
        log.info("Создан режиссер: {}", createdDirector);
        return directorMapper.mapToDirectorDto(createdDirector);
    }

    @Override
    public void delete(Long id) {
        directorStorage.delete(id);
        log.info("Удален режиссер с ID {}", id);
    }

    @Override
    public DirectorDto update(UpdateDirectorRequest updateRequestDirector) {
        Director directorToUpdate = getDirectorOrThrow(updateRequestDirector.getId());
        directorMapper.updateDirectorFromRequest(directorToUpdate, updateRequestDirector);
        Director updatedDirector = directorStorage.update(directorToUpdate);
        log.info("Обновлён режиссер: {}", updatedDirector);
        return directorMapper.mapToDirectorDto(updatedDirector);
    }

    @Override
    public DirectorDto getDirector(Long id) {
        return directorMapper.mapToDirectorDto(getDirectorOrThrow(id));
    }

    @Override
    public Director getDirectorOrThrow(Long id) {
        Director director = requireFound(directorStorage.getById(id), () -> "Режиссер с ID " + id + " не найден");
        log.info("Получен режиссер по ID {}: {}", id, director);
        return director;
    }
}
