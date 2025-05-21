package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IdValidator.class)
public @interface IdValid {
    String value() default "id";

    String message() default ""; // Сообщение по умолчанию не используется, всегда явно переопределяется в валидаторе

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
