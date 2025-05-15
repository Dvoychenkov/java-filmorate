package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.util.Collection;

public interface FeedStorage {
    void addEvent(FeedEvent event);

    Collection<FeedEvent> findByUserId(Long userId);
}
