package ru.yandex.practicum.filmorate.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.enums.SortOption;

import static ru.yandex.practicum.filmorate.converters.ConvertersHelper.getEnumNamesFormatted;

@Component
public class StringToSortOptionConverter implements Converter<String, SortOption> {
    @Override
    public SortOption convert(String source) {
        try {
            return SortOption.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    String.format("Недопустимое значение параметра 'sortBy': %s. Ожидаются значения из списка: %s",
                            source, getEnumNamesFormatted(SortOption.class))
            );
        }
    }
}
