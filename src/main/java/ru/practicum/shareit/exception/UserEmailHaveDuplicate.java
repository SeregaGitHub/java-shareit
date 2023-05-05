package ru.practicum.shareit.exception;

public class UserEmailHaveDuplicate extends RuntimeException {
    public UserEmailHaveDuplicate(String message) {
        super(message);
    }
}
