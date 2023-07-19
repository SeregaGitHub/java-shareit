package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.RequestParamError;
import ru.practicum.shareit.util.mark.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        try {
            BookingState state = BookingState.from(stateParam)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
            log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
            return bookingClient.getBookings(userId, state, from, size);
        } catch (Exception e) {
            return new ResponseEntity<>(new RequestParamError("Unknown state: " + stateParam), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Validated(Create.class) BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnerBooking(@RequestHeader("X-Sharer-User-Id") Integer owner,
                                            @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                            @Positive @RequestParam(value = "size",
                                                    defaultValue = "2147483647") Integer size) {
        try {
            BookingState state = BookingState.from(stateParam)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
            log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, owner, from, size);
            return bookingClient.getAllOwnerBooking(owner, state, from, size);
        } catch (Exception e) {
            return new ResponseEntity<>(new RequestParamError("Unknown state: " + stateParam), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setNewStatus(@RequestHeader("X-Sharer-User-Id") Integer owner,
                                               @PathVariable("bookingId") Integer bookingId,
                                               @RequestParam(value = "approved") Boolean approved) {
        log.info("Setting new status for booking with Id={}", bookingId);
        return bookingClient.setNewStatus(owner, bookingId, approved);
    }
}
