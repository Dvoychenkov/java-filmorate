package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Collection<Review> getAll(int count);

    Optional<Review> getById(Long id);

    Review add(Review review);

    Review update(Review review);

    void deleteById(Long id);

    Collection<Review> findAllByFilmId(Long filmId, int count);

    boolean addLike(Long reviewId, Long userId);

    boolean addDislike(Long reviewId, Long userId);

    boolean removeVote(Long reviewId, Long userId);
}
