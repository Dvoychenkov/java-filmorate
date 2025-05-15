package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;

import java.util.Collection;

public interface ReviewService {
    Collection<ReviewDto> getAll(Long filmId, Integer reviewsCount);

    ReviewDto create(NewReviewRequest newRequestReview);

    ReviewDto update(UpdateReviewRequest updateRequestReview);

    void delete(Long reviewId);

    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void removeVote(Long reviewId, Long userId);

    ReviewDto getReview(Long id);
}
