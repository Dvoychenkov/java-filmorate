package ru.yandex.practicum.filmorate.storage.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.base.BaseCRUDRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
@Slf4j
public class ReviewDbStorage extends BaseCRUDRepository<Review> implements ReviewStorage {
    // Обработка отзывов
    private static final String SQL_SELECT_ALL_REVIEWS = """
                SELECT * FROM films_users_reviews
                ORDER BY useful DESC
                LIMIT ?
            """;
    private static final String SQL_SELECT_REVIEW_BY_ID = "SELECT * FROM films_users_reviews WHERE id = ?";
    private static final String SQL_INSERT_REVIEW = """
                INSERT INTO films_users_reviews (film_id, user_id, content, is_positive, useful)
                VALUES (?, ?, ?, ?, ?)
            """;
    private static final String SQL_UPDATE_REVIEW = """
                UPDATE films_users_reviews SET content = ?, is_positive = ?
                WHERE id = ?
            """;
    private static final String SQL_DELETE_REVIEW = "DELETE FROM films_users_reviews WHERE id = ?";
    private static final String SQL_SELECT_ALL_REVIEWS_BY_FILM_ID = """
                SELECT * FROM films_users_reviews
                WHERE film_id = ?
                ORDER BY useful DESC
                LIMIT ?
            """;
    private static final String SQL_UPDATE_USEFUL_FOR_REVIEW = """
                UPDATE films_users_reviews
                SET useful = useful + ?
                WHERE id = ?
            """;

    // Обработка голосов для отзывов
    private static final String SQL_INSERT_VOTE = """
                INSERT INTO review_likes (review_id, user_id, is_like)
                VALUES (?, ?, ?)
            """;
    private static final String SQL_UPDATE_VOTE = """
                UPDATE review_likes
                SET is_like = ?
                WHERE review_id = ? AND user_id = ?
            """;
    private static final String SQL_DELETE_VOTE = """
                DELETE FROM review_likes
                WHERE review_id = ? AND user_id = ?
            """;
    private static final String SQL_SELECT_VOTE = """
                SELECT is_like
                FROM review_likes
                WHERE review_id = ?
                AND user_id = ?
            """;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, ReviewRowMapper rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public Collection<Review> getAll(int count) {
        Collection<Review> reviews = queryMany(SQL_SELECT_ALL_REVIEWS, count);
        log.info("Получено {} отзывов", reviews.size());
        return reviews;
    }

    @Override
    public Review add(Review review) {
        Long id = insertAndReturnId(SQL_INSERT_REVIEW,
                review.getFilmId(),
                review.getUserId(),
                review.getContent(),
                review.getIsPositive(),
                0 // начальное значение useful (Рейтинг отзыва)
        );

        if (id == null) return null;
        review.setId(id);
        review.setUseful(0);

        log.info("Отзыв добавлен: {}", review);
        return review;
    }

    @Override
    public Review update(Review review) {
        update(SQL_UPDATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getId()
        );
        log.info("Отзыв обновлён: {}", review);
        return review;
    }

    @Override
    public Optional<Review> getById(Long id) {
        Optional<Review> optReview = queryOne(SQL_SELECT_REVIEW_BY_ID, id);
        optReview.ifPresentOrElse(
                (review) -> log.info("Отзыв с ID {} найден: {}", id, review),
                () -> log.info("Отзыв с ID {} не найден", id)
        );
        return optReview;
    }

    @Override
    public void deleteById(Long id) {
        update(SQL_DELETE_REVIEW, id);
        log.info("Отзыв с ID {} удалён", id);
    }

    @Override
    public Collection<Review> findAllByFilmId(Long filmId, int count) {
        Collection<Review> reviews = queryMany(SQL_SELECT_ALL_REVIEWS_BY_FILM_ID, filmId, count);
        log.info("Получено {} отзывов для фильма {}", filmId, reviews.size());
        return reviews;
    }

    @Override
    public boolean addLike(Long reviewId, Long userId) {
        return vote(reviewId, userId, true);
    }

    @Override
    public boolean addDislike(Long reviewId, Long userId) {
        return vote(reviewId, userId, false);
    }

    @Override
    public boolean removeVote(Long reviewId, Long userId) {
        Optional<Boolean> existing = getUserVoteType(reviewId, userId);
        if (existing.isPresent()) {
            update(SQL_DELETE_VOTE, reviewId, userId);
            updateUseful(reviewId, existing.get() ? -1 : 1);
            log.info("Пользователь {} убрал голос за отзыв {}", userId, reviewId);
            return true;
        }
        return false;
    }

    private boolean vote(Long reviewId, Long userId, boolean isLike) {
        Optional<Boolean> existingVote = getUserVoteType(reviewId, userId);

        if (existingVote.isEmpty()) { // Новый голос
            update(SQL_INSERT_VOTE, reviewId, userId, isLike);
            updateUseful(reviewId, isLike ? 1 : -1);
            log.info("Пользователь {} поставил {} отзыву {}", userId, isLike ? "лайк" : "дизлайк", reviewId);
            return true;
        } else if (existingVote.get() == isLike) { // Голос уже есть и такой же — игнорируем
            log.info("Пользователь {} уже ставил {} отзыву {}", userId, isLike ? "лайк" : "дизлайк", reviewId);
            return false;
        } else { // Смена голоса на противоположный
            update(SQL_UPDATE_VOTE, isLike, reviewId, userId);
            updateUseful(reviewId, isLike ? 2 : -2); // +1 новый, -1 старый
            log.info("Пользователь {} сменил голос на {} для отзыва {}", userId, isLike ? "лайк" : "дизлайк", reviewId);
            return true;
        }
    }

    private Optional<Boolean> getUserVoteType(Long reviewId, Long userId) {
        Collection<Boolean> result = jdbcTemplate.query(SQL_SELECT_VOTE,
                (rs, rowNum) -> rs.getBoolean("is_like"),
                reviewId, userId);
        return result.stream().findFirst();
    }

    private void updateUseful(Long reviewId, int delta) {
        update(SQL_UPDATE_USEFUL_FOR_REVIEW, delta, reviewId);
    }
}
