package ru.practicum.shareit.exception;

public class RequestPaginationException extends RuntimeException {
    public RequestPaginationException(String message) {
        super(message);
    }
}
