package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedServiceBase implements FeedService {
    private final FeedStorage feedStorage;

    @Override
    public void addEvent(FeedEvent event) {
        feedStorage.addEvent(event);
    }

    @Override
    public Collection<FeedEvent> getFeedByUserId(Long userId) {
        return feedStorage.findByUserId(userId);
    }
}
