package ru.practicum.shareit.exception;

public class BookingErrorException extends RuntimeException {
    public BookingErrorException(String message) {
        super(message);
    }
}
