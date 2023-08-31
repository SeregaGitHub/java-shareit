package ru.practicum.shareit.booking.bookingUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.itemUtil.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {
    private User booker;
    private Item item;
    private BookingDto bookingDto;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(0, "name", "user@yandex.ru");
        booker = new User(1, "requesterName", "requester@yandex.ru");
        ItemRequest itemRequest = new ItemRequest(0, "itemN", now, booker);
        ItemWithRequestDto itemWithRequestDto = new ItemWithRequestDto(0, "itemName", "itemDescription", true,
                booker.getId());
        item = ItemMapper.toItem(user, itemWithRequestDto, itemRequest);
        BookingForItemDto bookingForItemDto = BookingForItemDto.builder()
                .id(1)
                .startTime(now.plusHours(1))
                .endTime(now.plusHours(2))
                .itemId(item.getId())
                .bookerId(booker.getId())
                .build();
        bookingDto = BookingDto.builder()
                .id(1)
                .start(now.plusHours(1))
                .end(now.plusHours(2))
                .itemId(item.getId())
                .build();
        booking = Booking.builder()
                .id(1)
                .start(now.plusHours(1))
                .end(now.plusHours(2))
                .status(Status.WAITING)
                .item(item)
                .booker(booker)
                .build();
    }

    @Test
    void toBooking() {
        Booking bookingCheck = BookingMapper.toBooking(booker, bookingDto, item);

        assertEquals(booking, bookingCheck);
    }

    @Test
    void toBookingDto() {
        BookingDto bookingDtoCheck = BookingMapper.toBookingDto(booking);

        assertEquals(bookingDto, bookingDtoCheck);
    }
}