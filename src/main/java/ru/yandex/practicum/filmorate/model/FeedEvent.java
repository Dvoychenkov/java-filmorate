package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedEvent {
    private Long eventId;
    private Long userId;
    private FeedEventType eventType;
    private FeedOperation operation;
    private Long entityId;
    private Long timestamp;

    public FeedEvent(Long userId, FeedEventType eventType, FeedOperation operation, Long entityId) {
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}
