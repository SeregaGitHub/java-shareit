package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.RequestPaginationException;

import static org.junit.jupiter.api.Assertions.*;

class RequestPaginationValidTest {

    @Test
    void requestPaginationValid_whenFromIsNotPositive_thenThrowException() {
        RequestPaginationException exception = assertThrows(RequestPaginationException.class,
                () -> RequestPaginationValid.requestPaginationValid(-1, 1));

        assertEquals("Индекс начала списка не может быть меньше нуля", exception.getMessage());
    }

    @Test
    void requestPaginationValid_whenSizeIsNotPositive_thenThrowException() {
        RequestPaginationException exception = assertThrows(RequestPaginationException.class,
                () -> RequestPaginationValid.requestPaginationValid(0, -1));

        assertEquals("Количество запросов должно быть положительным числом", exception.getMessage());
    }

    @Test
    void requestPaginationValid_whenThereIsNoFirstArgument_thenThrowException() {
        RequestPaginationException exception = assertThrows(RequestPaginationException.class,
                () -> RequestPaginationValid.requestPaginationValid(null, 1));

        assertEquals("В запросе должны присутствовать два аргумента: " +
                "\"Индекс начала списка\" и \"Количество запросов\"", exception.getMessage());
    }

    @Test
    void requestPaginationValid_whenThereIsNoSecondArgument_thenThrowException() {
        RequestPaginationException exception = assertThrows(RequestPaginationException.class,
                () -> RequestPaginationValid.requestPaginationValid(0, null));

        assertEquals("В запросе должны присутствовать два аргумента: " +
                "\"Индекс начала списка\" и \"Количество запросов\"", exception.getMessage());
    }
}