package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.services.ReviewService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public Collection<ReviewDto> getAll(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false) Integer count
    ) {
        return reviewService.getAll(filmId, count);
    }

    @GetMapping("/{id}")
    public ReviewDto getById(@PathVariable Long id) {
        return reviewService.getReview(id);
    }

    @PostMapping
    public ReviewDto create(@Valid @RequestBody NewReviewRequest request) {
        ReviewDto created = reviewService.create(request);
        log.info("Создан отзыв: {}", created);
        return created;
    }

    @PutMapping
    public ReviewDto update(@Valid @RequestBody UpdateReviewRequest request) {
        ReviewDto updated = reviewService.update(request);
        log.info("Обновлён отзыв: {}", updated);
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
        log.info("Удалён отзыв с ID {}", id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeVote(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeVote(id, userId);
    }
}
