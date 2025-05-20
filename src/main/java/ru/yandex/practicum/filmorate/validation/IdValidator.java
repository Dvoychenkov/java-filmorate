package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IdValidator implements ConstraintValidator<IdValid, Long> {
    private String paramName;

    @Override
    public void initialize(IdValid constraintAnnotation) {
        this.paramName = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value != null) return true;

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(paramName + " не может быть null").addConstraintViolation();
        return false;
    }
}
