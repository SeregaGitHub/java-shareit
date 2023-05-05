package ru.practicum.shareit.booking.bookingUtil;

import ru.practicum.shareit.exception.BookingErrorException;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.Objects;

public class BookingUtil {
    public static boolean checkStartAndEndTime(LocalDateTime start, LocalDateTime end) {
        return start.isBefore(end);
    }

    public static void checkOwner(Integer bookerId, Integer ownerId) {
        if (Objects.equals(bookerId, ownerId)) {
            throw new NotFoundException("Owner can not booking his own item");
        }
    }

    public static State makeState(String stateString) {
        State state;
        try {
            state = State.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new BookingErrorException("Unknown state: " + stateString);
        }
        return state;
    }
}
