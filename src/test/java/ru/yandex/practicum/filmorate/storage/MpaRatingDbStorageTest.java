package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingRowMapper;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaRatingDbStorage.class, MpaRatingRowMapper.class})
class MpaRatingDbStorageTest {
    private final MpaRatingDbStorage mpaStorage;

    @Test
    void shouldReturnAllMpaRatings() {
        int ratingsSize = 5;
        Collection<MpaRating> ratings = mpaStorage.getAll();
        assertThat(ratings).isNotNull();
        assertThat(ratings).isNotEmpty();
        assertThat(ratings).hasSize(ratingsSize);
    }

    @Test
    void shouldReturnMpaRatingById() {
        MpaRating mpa1 = new MpaRating(1L, "G", "G",
                "General Audiences: Нет возрастных ограничений");

        Optional<MpaRating> optMpa = mpaStorage.getById(mpa1.getId());
        assertThat(optMpa)
                .isPresent()
                .contains(mpa1);

        MpaRating mpa5 = new MpaRating(5L, "NC_17", "NC-17",
                "Adults Only: До 18 лет запрещено");

        optMpa = mpaStorage.getById(mpa5.getId());
        assertThat(optMpa)
                .isPresent()
                .contains(mpa5);
    }
}
