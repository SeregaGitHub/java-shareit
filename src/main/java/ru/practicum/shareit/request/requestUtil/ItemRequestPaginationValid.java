package ru.practicum.shareit.request.requestUtil;

import ru.practicum.shareit.exception.ItemRequestPaginationException;

import java.util.Optional;

public class ItemRequestPaginationValid {
    public static void itemRequestPaginationValid(Integer from, Integer size) {
        Optional<Integer> optFrom = Optional.ofNullable(from);
        if (optFrom.isPresent()) {
            int fromCheck = optFrom.get();
            if (fromCheck < 0) {
                throw new ItemRequestPaginationException("Индекс начала списка не может быть меньше нуля");
            }
        }

        Optional<Integer> optSize = Optional.ofNullable(size);
        if (optSize.isPresent()) {
            int sizeCheck = optSize.get();
            if (sizeCheck <= 0) {
                throw new ItemRequestPaginationException("Количество запросов должно быть положительным числом");
            }
        }
    }
}
