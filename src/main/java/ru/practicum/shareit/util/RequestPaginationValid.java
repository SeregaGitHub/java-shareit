package ru.practicum.shareit.util;

import ru.practicum.shareit.exception.RequestPaginationException;

import java.util.Optional;

public class RequestPaginationValid {
    public static void requestPaginationValid(Integer from, Integer size) {
        Optional<Integer> optFrom = Optional.ofNullable(from);
        Optional<Integer> optSize = Optional.ofNullable(size);

        if (optFrom.isPresent()) {
            int fromCheck = optFrom.get();
            if (fromCheck < 0) {
                throw new RequestPaginationException("Индекс начала списка не может быть меньше нуля");
            }
        }
        if (optSize.isPresent()) {
            int sizeCheck = optSize.get();
            if (sizeCheck <= 0) {
                throw new RequestPaginationException("Количество запросов должно быть положительным числом");
            }
        }
        if (optFrom.isEmpty() && optSize.isPresent() || optFrom.isPresent() && optSize.isEmpty()) {
            throw new RequestPaginationException("В запросе должны присутствовать два аргумента: " +
                    "\"Индекс начала списка\" и \"Количество запросов\"");
        }
    }
}