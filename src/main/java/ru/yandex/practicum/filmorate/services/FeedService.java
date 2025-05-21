package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.util.Collection;

public interface FeedService {
    void addEvent(FeedEvent event);

    Collection<FeedEvent> getFeedByUserId(Long userId);
}
