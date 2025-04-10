package ru.yandex.practicum.filmorate.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationException extends RuntimeException {
    private final static Logger log = LoggerFactory.getLogger(ValidationException.class);

    public ValidationException(String message) {
        super(message);
        log.error("Ошибка валидации: {}", message);
    }
}