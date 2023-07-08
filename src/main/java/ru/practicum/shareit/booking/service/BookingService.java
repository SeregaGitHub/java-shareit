package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.transaction.Transactional;
import java.util.List;

public interface BookingService {
    @Transactional
    Booking addBooking(Integer booker, BookingDto bookingDto);

    Booking setNewStatus(Integer owner, Integer bookingId, Boolean approved);

    Booking getBooking(Integer userId, Integer bookingId);

    List<Booking> getAllUserBookings(Integer booker, String state, Integer from, Integer size);

    List<Booking> getAllOwnerBooking(Integer owner, String state, Integer from, Integer size);
}
