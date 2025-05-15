package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.ValidationUtils.requireFound;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceBase implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewMapper reviewMapper;
    private final UserService userService;
    private final FilmService filmService;

    @Override
    public Collection<ReviewDto> getAll(Long filmId, Integer reviewsCount) {
        // Если кол-во не указано, то по дефолту берём 10
        if (reviewsCount == null || reviewsCount <= 0) {
            log.info("Не передан корректный параметр count = {}, используется значение по умолчанию", reviewsCount);
            reviewsCount = 10;
        }

        // Если фильм не указан, то все отзывы
        Collection<Review> reviews;
        if (filmId == null) {
            reviews = reviewStorage.getAll(reviewsCount);
            log.info("Не указан фильм, получаем все отзывы");
        } else {
            reviews = reviewStorage.findAllByFilmId(filmId, reviewsCount);
            log.info("Указан фильм, получаем его отзывы");
        }

        log.info("Получено {} отзывов", reviews.size());
        return reviews.stream()
                .map(reviewMapper::mapToReviewDto)
                .toList();
    }

    @Override
    public ReviewDto create(NewReviewRequest newRequestReview) {
        userService.getUserOrThrow(newRequestReview.getUserId()); // Проверка на наличие пользователя
        filmService.getFilmOrThrow(newRequestReview.getFilmId()); // Проверка на наличие фильма

        Review reviewToCreate = reviewMapper.mapToReview(newRequestReview);
        Review createdReview = reviewStorage.add(reviewToCreate);
        if (createdReview == null) throw new IllegalStateException("Не удалось сохранить данные для нового отзыва");
        log.info("Создан отзыв: {}", createdReview);
        return reviewMapper.mapToReviewDto(createdReview);
    }

    @Override
    public ReviewDto update(UpdateReviewRequest updateRequestReview) {
        Review reviewToUpdate = getReviewOrThrow(updateRequestReview.getReviewId());
        reviewMapper.updateReviewFromRequest(reviewToUpdate, updateRequestReview);
        Review updatedReview = reviewStorage.update(reviewToUpdate);
        log.info("Обновлён отзыв: {}", updatedReview);
        return reviewMapper.mapToReviewDto(updatedReview);
    }

    @Override
    public void delete(Long reviewId) {
        getReviewOrThrow(reviewId); // Проверка на наличие отзыва
        reviewStorage.deleteById(reviewId);
        log.info("Отзыв с ID {} удалён", reviewId);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        validateUserAndReview(userId, reviewId);

        boolean added = reviewStorage.addLike(reviewId, userId);
        if (added) {
            log.info("Пользователь {} поставил лайк отзыву {}", userId, reviewId);
        } else {
            log.info("Пользователь {} уже ставил лайк отзыву {}", userId, reviewId);
        }
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        validateUserAndReview(userId, reviewId);

        boolean added = reviewStorage.addDislike(reviewId, userId);
        if (added) {
            log.info("Пользователь {} поставил дизлайк отзыву {}", userId, reviewId);
        } else {
            log.info("Пользователь {} уже ставил дизлайк отзыву {}", userId, reviewId);
        }
    }

    @Override
    public void removeVote(Long reviewId, Long userId) {
        validateUserAndReview(userId, reviewId);

        boolean removed = reviewStorage.removeVote(reviewId, userId);
        if (removed) {
            log.info("Пользователь {} убрал голос отзыву {}", userId, reviewId);
        } else {
            log.info("Пользователь {} не ставил раннее голос отзыву {}", userId, reviewId);
        }
    }

    @Override
    public ReviewDto getReview(Long id) {
        return reviewMapper.mapToReviewDto(getReviewOrThrow(id));
    }

    private Review getReviewOrThrow(Long id) {
        if (id == null) throw new ValidationException("Некорректный ID отзыва");
        Review review = requireFound(reviewStorage.getById(id), () -> "Отзыв с ID " + id + " не найден");
        log.info("Получен отзыв по ID {}: {}", id, review);
        return review;
    }

    private void validateUserAndReview(Long userId, Long reviewId) {
        userService.getUserOrThrow(userId); // Проверка на наличие пользователя
        getReviewOrThrow(reviewId); // Проверка на наличие отзыва
    }
}
