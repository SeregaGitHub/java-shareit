package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.bookingUtil.BookingMapper;
import ru.practicum.shareit.booking.bookingUtil.State;
import ru.practicum.shareit.booking.bookingUtil.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private User user;
    private BookingDto bookingDto;
    private Booking booking;
    private Item item;
    private User owner;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate localDate = now.toLocalDate();
        LocalTime localTime = LocalTime.parse(now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        LocalDateTime ldt = LocalDateTime.of(localDate, localTime);

        bookingDto = BookingDto.builder()
                .id(0)
                .start(ldt.plusHours(1))
                .end(ldt.plusHours(2))
                .itemId(3)
                .build();

        user = new User(0, "name", "email@yandex.ru");
        owner = new User(1, "owner", "owner@yandex.ru");
        User requester = new User(2, "requester", "requester@yandex.ru");
        ItemRequest itemRequest = new ItemRequest(0, "requestDescription", ldt, requester);
        item = new Item(3, "itemName", "itemDescription", true, owner, itemRequest);
        booking = BookingMapper.toBooking(user, bookingDto, item);
    }

    @Test
    void addBooking() {
        Integer bookerId = user.getId();
        when(bookingService.addBooking(bookerId, bookingDto)).thenReturn(booking);

        Booking returnedBooking = bookingController.addBooking(bookerId, bookingDto);

        assertEquals(booking, returnedBooking);
        verify(bookingService).addBooking(bookerId, bookingDto);
    }

    @Test
    void setNewStatus() {
        Integer ownerId = owner.getId();
        Integer bookingId = booking.getId();
        Booking approvedBooking = BookingMapper.toBooking(user, bookingDto, item);
        approvedBooking.setStatus(Status.APPROVED);
        when(bookingService.setNewStatus(ownerId, bookingId, true)).thenReturn(approvedBooking);

        Booking returnedBooking = bookingController.setNewStatus(ownerId, bookingId, true);

        assertEquals(approvedBooking, returnedBooking);
        verify(bookingService, times(1)).setNewStatus(ownerId, bookingId, true);
    }

    @Test
    void getBooking() {
        Integer ownerId = owner.getId();
        Integer bookingId = booking.getId();
        when(bookingService.getBooking(ownerId, bookingId)).thenReturn(booking);

        Booking returnedBooking = bookingController.getBooking(ownerId, bookingId);

        assertEquals(booking, returnedBooking);
        verify(bookingService, times(1)).getBooking(ownerId, bookingId);
    }

    @Test
    void getAllUserBooking() {
        Integer bookerId = user.getId();
        when(bookingService.getAllUserBookings(bookerId, State.ALL.toString(), 0, 1)).thenReturn(List.of(booking));

        List<Booking> returnedBookingList = bookingController.getAllUserBooking(bookerId, State.ALL.toString(), 0, 1);

        assertEquals(1, returnedBookingList.size());
        verify(bookingService, times(1)).getAllUserBookings(bookerId, State.ALL.toString(), 0, 1);
    }

    @Test
    void getAllOwnerBooking() {
        Integer ownerId = owner.getId();
        when(bookingService.getAllOwnerBooking(ownerId, State.ALL.toString(), 0, 1)).thenReturn(List.of(booking));

        List<Booking> returnedBookingList = bookingController.getAllOwnerBooking(ownerId, State.ALL.toString(), 0, 1);

        assertEquals(1, returnedBookingList.size());
        verify(bookingService, times(1)).getAllOwnerBooking(ownerId, State.ALL.toString(), 0, 1);
    }
}