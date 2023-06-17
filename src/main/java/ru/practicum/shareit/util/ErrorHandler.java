package ru.practicum.shareit.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    private static final String ERROR_MESSAGE = "errorMessage - ";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(NotFoundException exception) {
        return Map.of(
                ERROR_MESSAGE, exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleUserEmailHaveDuplicate(UserEmailHaveDuplicate exception) {
        return Map.of(
                ERROR_MESSAGE, exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNullField(ConstraintViolationException exception) {
        return Map.of(
                ERROR_MESSAGE, exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoOwner(OwnerNotFoundException exception) {
        return Map.of(
                ERROR_MESSAGE, exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleTimeError(BookingErrorException exception) {
        return Map.of(
                "error", exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleForbiddenError(ForbiddenException exception) {
        return Map.of(
                ERROR_MESSAGE, exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCommentError(CommentErrorException exception) {
        return Map.of(
                ERROR_MESSAGE, exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleItemRequestPagination(RequestPaginationException exception) {
        return Map.of(
                ERROR_MESSAGE, exception.getMessage()
        );
    }
}
