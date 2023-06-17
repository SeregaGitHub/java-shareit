package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.bookingUtil.BookingMapper;
import ru.practicum.shareit.booking.bookingUtil.State;
import ru.practicum.shareit.booking.bookingUtil.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.itemUtil.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingHibernateServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingHibernateService bookingHibernateService;
    private User user;
    private User requester;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        user = new User(0, "name", "user@yandex.ru");
        requester = new User(1, "requesterName", "requester@yandex.ru");
        ItemRequest itemRequest = new ItemRequest(0, "itemN", now, requester);
        ItemWithRequestDto itemWithRequestDto = new ItemWithRequestDto(1, "itemName", "itemDescription", true,
                requester.getId());
        item = ItemMapper.toItem(user, itemWithRequestDto, itemRequest);
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = now.plusHours(2);
        booking = new Booking(0, startTime, endTime, item, requester, Status.WAITING);
        bookingDto = BookingDto.builder()
                .id(0)
                .start(startTime)
                .end(endTime)
                .itemId(item.getId())
                .build();
    }

    @Test
    void addBooking() {

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRepository.findItemByIdWithOwner(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(BookingMapper.toBooking(requester, bookingDto, item)))
                .thenReturn(booking);

        Booking returnedBooking = bookingHibernateService.addBooking(requester.getId(), bookingDto);
        assertEquals(booking, returnedBooking);

        verify(userRepository, times(1)).findById(requester.getId());
        verify(itemRepository, times(1)).findItemByIdWithOwner(item.getId());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void setNewStatus_whenBookingNotFound_thenReturnNotFoundException() {
        when(bookingRepository.getBooking(booking.getId())).thenThrow(new NotFoundException("NotFoundException"));

        assertThrows(NotFoundException.class, () -> bookingHibernateService.setNewStatus(
                booking.getItem().getOwner().getId(), booking.getId(), true));

        verify(bookingRepository, times(1)).getBooking(booking.getId());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void setNewStatus_whenBookingIsExist_whenReturnApprovedBooking() {
        when(bookingRepository.getBooking(booking.getId())).thenReturn(Optional.of(booking));

        bookingHibernateService.setNewStatus(booking.getItem().getOwner().getId(), booking.getId(), true);

        verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());
        Booking captorBooking = bookingArgumentCaptor.getValue();
        assertEquals(booking.getId(), captorBooking.getId());
        assertEquals(Status.APPROVED, captorBooking.getStatus());
    }

    @Test
    void getBooking_whenBookingNotFound_thenReturnNotFoundException() {
        when(bookingRepository.getBooking(booking.getId())).thenThrow(new NotFoundException("NotFoundException"));

        assertThrows(NotFoundException.class, () -> bookingHibernateService.setNewStatus(
                booking.getItem().getOwner().getId(), booking.getId(), true));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void getBooking_whenBookingIsExist_whenReturnBooking() {
        Integer bookingId = booking.getId();
        when(bookingRepository.getBooking(bookingId)).thenReturn(Optional.of(booking));

        Booking returnedBooking = bookingHibernateService.getBooking(booking.getBooker().getId(), bookingId);

        assertEquals(booking, returnedBooking);
        verify(bookingRepository, times(1)).getBooking(bookingId);
    }

    @Test
    void getAllUserBookings() {
        Integer requesterId = requester.getId();
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getAllUserBookings(requesterId, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingHibernateService.getAllUserBookings(
                requesterId, State.ALL.toString(), 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getAllUserBookings(
                requesterId, PageRequest.of(0, 1));
    }

    @Test
    void getAllOwnerBooking() {
        Integer ownerId = user.getId();
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(bookingRepository.getAllUserBookings(ownerId, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingHibernateService.getAllUserBookings(
                ownerId, State.ALL.toString(), 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, times(1)).getAllUserBookings(
                ownerId, PageRequest.of(0, 1));
    }
}