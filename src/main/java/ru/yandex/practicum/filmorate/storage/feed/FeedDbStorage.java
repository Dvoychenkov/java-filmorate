package ru.yandex.practicum.filmorate.storage.feed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.storage.base.BaseCRUDRepository;

import java.sql.Timestamp;
import java.util.Collection;

@Repository
@Slf4j
public class FeedDbStorage extends BaseCRUDRepository<FeedEvent> implements FeedStorage {
    private static final String SQL_INSERT_EVENT = """
                INSERT INTO feed (timestamp, user_id, event_type, operation, entity_id)
                VALUES (?, ?, ?, ?, ?)
            """;

    private static final String SQL_SELECT_BY_USER_ID = """
                SELECT * FROM feed
                WHERE user_id = ?
                ORDER BY timestamp ASC
            """;

    public FeedDbStorage(JdbcTemplate jdbcTemplate, FeedRowMapper feedRowMapper) {
        super(jdbcTemplate, feedRowMapper);
    }

    @Override
    public void addEvent(FeedEvent feedEvent) {
        if (feedEvent.getTimestamp() == null) feedEvent.setTimestamp(System.currentTimeMillis());

        Long id = insertAndReturnId(SQL_INSERT_EVENT,
                new Timestamp(feedEvent.getTimestamp()),
                feedEvent.getUserId(),
                feedEvent.getEventType().name(),
                feedEvent.getOperation().name(),
                feedEvent.getEntityId());
        if (id == null) throw new IllegalStateException("Не удалось сохранить данные для события в feed");

        feedEvent.setEventId(id);
        log.info("Событие добавлено в ленту: {}", feedEvent);
    }

    @Override
    public Collection<FeedEvent> findByUserId(Long userId) {
        Collection<FeedEvent> feedEvents = queryMany(SQL_SELECT_BY_USER_ID, userId);
        log.info("Получено {} событий ленты юзера", feedEvents.size());
        return feedEvents;
    }
}
