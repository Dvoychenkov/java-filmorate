package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorRowMapper;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.yandex.practicum.filmorate.util.TestHelper.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
    DirectorDbStorage.class, DirectorRowMapper.class})
public class DirectorDbStorageTest {
    private final DirectorDbStorage directorStorage;

    @Test
    void shouldReturnAllDirectors() {
        Collection<Director> initDirectors = directorStorage.getAll();
        assertThat(initDirectors).isNotNull();
        assertThat(initDirectors).isEmpty();

        int addedCnt = 10;
        for (long i = 1; i <= addedCnt; i++) {
            directorStorage.add(generateDirector());
        }

        Collection<Director> allDirectors = directorStorage.getAll();
        assertThat(allDirectors).isNotNull();
        assertThat(allDirectors).isNotEmpty();
        assertThat(allDirectors).hasSize(addedCnt);
    }

    @Test
    void shouldAddAndGetDirector() {
        Director directorToCreate = generateDirector();

        Director createdDirector = directorStorage.add(directorToCreate);
        Optional<Director> createdDirectorFromDB = directorStorage.getById(createdDirector.getId());

        assertThat(createdDirectorFromDB)
                .isPresent()
                .contains(createdDirector);
    }

    @Test
    void shouldUpdateDirector() {
        Director directorToCreate = generateDirector();
        Director createdDirector = directorStorage.add(directorToCreate);

        Director directorToUpdate = generateDirector();
        directorToUpdate.setId(createdDirector.getId());
        Director updatedDirector = directorStorage.update(directorToUpdate);

        Optional<Director> updatedDirectorFromDB = directorStorage.getById(updatedDirector.getId());
        assertThat(updatedDirectorFromDB)
                .isPresent()
                .contains(directorToUpdate);
    }

    @Test
    void shouldDeleteDirector() {
        Director directorToCreate = generateDirector();
        Director createdDirector = directorStorage.add(directorToCreate);

        directorStorage.delete(createdDirector.getId());

        Optional<Director> deletedDirectorFromDB = directorStorage.getById(createdDirector.getId());
        assertThat(deletedDirectorFromDB)
                .isEmpty();
    }
}
