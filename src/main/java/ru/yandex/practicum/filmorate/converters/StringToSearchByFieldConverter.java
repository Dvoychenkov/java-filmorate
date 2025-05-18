package ru.yandex.practicum.filmorate.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.enums.SearchByField;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class StringToSearchByFieldConverter implements Converter<String, SearchByField> {
    @Override
    public SearchByField convert(String source) {
        try {
            return SearchByField.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException e) {

            throw new ValidationException(
                    String.format(
                            "Недопустимое значение параметра 'by': %s. Ожидаются значения из списка: %s",
                            source,
                            Arrays.stream(SearchByField.values())
                                    .map(Enum::name)
                                    .map(String::toLowerCase)
                                    .collect(Collectors.joining(", ", "[", "]"))
                    )
            );
        }
    }
}