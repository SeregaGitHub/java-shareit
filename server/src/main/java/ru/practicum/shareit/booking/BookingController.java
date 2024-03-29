package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking addBooking(@RequestHeader("X-Sharer-User-Id") Integer booker,
                              @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(booker, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking setNewStatus(@RequestHeader("X-Sharer-User-Id") Integer owner,
                                @PathVariable("bookingId") Integer bookingId,
                                @RequestParam("approved") Boolean approved) {
        return bookingService.setNewStatus(owner, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable("bookingId") Integer bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getAllUserBooking(@RequestHeader("X-Sharer-User-Id") Integer booker,
                                           @RequestParam(value = "state", defaultValue = "ALL") String state,
                                           @RequestParam(value = "from", defaultValue = "0") Integer from,
                                           @RequestParam(value = "size",
                                                   defaultValue = "2147483647") Integer size) {
        return bookingService.getAllUserBookings(booker, state, from, size);
    }

    @GetMapping("/owner")
    public List<Booking> getAllOwnerBooking(@RequestHeader("X-Sharer-User-Id") Integer owner,
                                            @RequestParam(value = "state", defaultValue = "ALL") String state,
                                            @RequestParam(value = "from", defaultValue = "0") Integer from,
                                            @RequestParam(value = "size",
                                                    defaultValue = "2147483647") Integer size) {
        return bookingService.getAllOwnerBooking(owner, state, from, size);
    }
}