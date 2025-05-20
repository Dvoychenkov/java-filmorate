package ru.yandex.practicum.filmorate.converters;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ConvertersHelper {
    private ConvertersHelper() {
    }

    public static <T extends Enum<T>> String getEnumNamesFormatted(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
