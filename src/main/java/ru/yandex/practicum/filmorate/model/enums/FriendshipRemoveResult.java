package ru.yandex.practicum.filmorate.model.enums;

public enum FriendshipRemoveResult {
    CONFIRMED_FRIENDSHIP_REMOVED,   // Дружба отменена
    FRIEND_REQUEST_REMOVED,         // Заявка отменена
    NO_FRIENDSHIP,                  // Дружбы/заявок не было
    UNKNOWN                         // Неизвестное состояние
}
