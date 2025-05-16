package ru.yandex.practicum.filmorate.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.enums.SortOption;

@Component
public class StringToSortOptionConverter implements Converter<String, SortOption> {
    @Override
    public SortOption convert(String source) {
        return SortOption.valueOf(source.toUpperCase());
    }
}
