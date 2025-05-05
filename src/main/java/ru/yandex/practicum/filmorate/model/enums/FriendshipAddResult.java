package ru.yandex.practicum.filmorate.model.enums;

public enum FriendshipAddResult {
    FRIEND_REQUEST_ADDED,           // Отправлена новая заявка
    FRIEND_REQUEST_ALREADY_EXISTS,  // Мы уже отправляли заявку ранее
    FRIENDSHIP_CONFIRMED,           // Заявка взаимная — дружба подтверждена
    FRIENDSHIP_ALREADY_EXISTS,      // Дружба уже была подтверждена
    UNKNOWN                         // Неизвестное состояние
}
