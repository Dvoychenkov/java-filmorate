package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper implements RowMapper<FeedEvent> {
    @Override
    public FeedEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        FeedEvent event = new FeedEvent();
        event.setEventId(rs.getLong("id"));
        event.setUserId(rs.getLong("user_id"));
        event.setTimestamp(rs.getTimestamp("timestamp").getTime());
        event.setEntityId(rs.getLong("entity_id"));

        try {
            event.setEventType(FeedEventType.valueOf(rs.getString("event_type")));
        } catch (IllegalArgumentException e) {
            throw new SQLException("Неизвестный тип события в БД: " + rs.getString("event_type"), e);
        }
        try {
            event.setOperation(FeedOperation.valueOf(rs.getString("operation")));
        } catch (IllegalArgumentException e) {
            throw new SQLException("Неизвестная тип операции в БД: " + rs.getString("operation"), e);
        }

        return event;
    }
}
