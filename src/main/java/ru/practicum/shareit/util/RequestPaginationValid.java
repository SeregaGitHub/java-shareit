package ru.practicum.shareit.util;

import ru.practicum.shareit.exception.RequestPaginationException;

import java.util.Optional;

public class RequestPaginationValid {
    public static void requestPaginationValid(Integer from, Integer size) {
        Optional<Integer> optFrom = Optional.ofNullable(from);
        if (optFrom.isPresent()) {
            int fromCheck = optFrom.get();
            if (fromCheck < 0) {
                throw new RequestPaginationException("Индекс начала списка не может быть меньше нуля");
            }
        }

        Optional<Integer> optSize = Optional.ofNullable(size);
        if (optSize.isPresent()) {
            int sizeCheck = optSize.get();
            if (sizeCheck <= 0) {
                throw new RequestPaginationException("Количество запросов должно быть положительным числом");
            }
        }
    }
}
