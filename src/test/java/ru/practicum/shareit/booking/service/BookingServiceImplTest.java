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
import ru.practicum.shareit.exception.BookingErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.RequestPaginationException;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.itemUtil.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private Clock clock;
    @InjectMocks
    private BookingServiceImpl bookingServiceImpl;
    private User user;
    private User requester;
    private Item item;
    private Booking booking;
    private LocalDateTime now;
    private LocalDateTime startTimeFuture;
    private LocalDateTime endTimeFuture;
    private BookingDto bookingDto;
    private static final ZonedDateTime NOW_ZDT = ZonedDateTime.of(
            2023,
            10,
            10,
            10,
            10,
            10,
            0,
            ZoneId.of("UTC")
    );
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.of(2023,
                10,
                10,
                10,
                10,
                10);
        user = new User(0, "name", "user@yandex.ru");
        requester = new User(1, "requesterName", "requester@yandex.ru");
        ItemRequest itemRequest = new ItemRequest(0, "itemN", now, requester);
        ItemWithRequestDto itemWithRequestDto = new ItemWithRequestDto(1, "itemName", "itemDescription", true,
                requester.getId());
        item = ItemMapper.toItem(user, itemWithRequestDto, itemRequest);
        startTimeFuture = now.plusHours(1);
        endTimeFuture = now.plusHours(2);
        booking = new Booking(0, startTimeFuture, endTimeFuture, item, requester, Status.WAITING);
        bookingDto = BookingDto.builder()
                .id(0)
                .start(startTimeFuture)
                .end(endTimeFuture)
                .itemId(item.getId())
                .build();
    }

    @Test
    void addBooking_whenItemIdIsNotPositive_thenThrowException() {
        bookingDto.setItemId(-1);

        assertThrows(BookingErrorException.class,
                () -> bookingServiceImpl.addBooking(requester.getId(), bookingDto));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void addBooking_whenUserWasNotFound_thenThrowException() {
        Integer bookerId = requester.getId();
        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.addBooking(bookerId, bookingDto));

        assertEquals("User with Id=" + bookerId + " - does not exist", exception.getMessage());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void addBooking_whenItemWasNotFound_thenThrowException() {
        Integer bookerId = requester.getId();
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(itemRepository.findItemByIdWithOwner(bookingDto.getItemId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.addBooking(bookerId, bookingDto));

        assertEquals("Item with Id=" + bookingDto.getItemId() + " - does not exist", exception.getMessage());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void addBooking_whenItemAvailableIsFalse_thenThrowException() {
        Integer bookerId = requester.getId();
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(itemRepository.findItemByIdWithOwner(bookingDto.getItemId())).thenReturn(Optional.of(item));
        item.setAvailable(false);

        BookingErrorException exception = assertThrows(BookingErrorException.class,
                () -> bookingServiceImpl.addBooking(bookerId, bookingDto));

        assertEquals("Item is not available", exception.getMessage());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void addBooking_whenStartAndEndTimeNotCorrespond_thenThrowException() {
        bookingDto.setStart(endTimeFuture);
        bookingDto.setEnd(startTimeFuture);

        assertThrows(BookingErrorException.class,
                () -> bookingServiceImpl.addBooking(requester.getId(), bookingDto));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void addBooking_whenOwnerTryToBookingHisOwnItem_thenThrowException() {

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRepository.findItemByIdWithOwner(item.getId())).thenReturn(Optional.of(item));
        item.getOwner().setId(requester.getId());

        assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.addBooking(item.getOwner().getId(), bookingDto));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void addBooking_whenAllConditionsCorrespond_whenReturnBooking() {

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRepository.findItemByIdWithOwner(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(BookingMapper.toBooking(requester, bookingDto, item)))
                .thenReturn(booking);

        Booking returnedBooking = bookingServiceImpl.addBooking(requester.getId(), bookingDto);
        assertEquals(booking, returnedBooking);

        verify(userRepository, times(1)).findById(requester.getId());
        verify(itemRepository, times(1)).findItemByIdWithOwner(item.getId());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void setNewStatus_whenBookingNotFound_thenReturnNotFoundException() {
        when(bookingRepository.getBooking(booking.getId())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.setNewStatus(
                booking.getItem().getOwner().getId(), booking.getId(), true));

        assertEquals("Booking with Id=" + booking.getId() + " does not exist", notFoundException.getMessage());
        verify(bookingRepository, times(1)).getBooking(booking.getId());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void setNewStatus_whenBookingStatusIsNotWAITING_thenReturnNotFoundException() {
        when(bookingRepository.getBooking(booking.getId())).thenReturn(Optional.of(booking));
        booking.setStatus(Status.REJECTED);

        BookingErrorException exception = assertThrows(BookingErrorException.class,
                () -> bookingServiceImpl.setNewStatus(
                        booking.getItem().getOwner().getId(), booking.getId(), true));

        assertEquals("New status has already been confirmed", exception.getMessage());
        verify(bookingRepository, times(1)).getBooking(booking.getId());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void setNewStatus_whenBookingIsExist_whenReturnApprovedBooking() {
        when(bookingRepository.getBooking(booking.getId())).thenReturn(Optional.of(booking));

        bookingServiceImpl.setNewStatus(booking.getItem().getOwner().getId(), booking.getId(), true);

        verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());
        Booking captorBooking = bookingArgumentCaptor.getValue();
        assertEquals(booking.getId(), captorBooking.getId());
        assertEquals(Status.APPROVED, captorBooking.getStatus());
    }

    @Test
    void getBooking_whenBookingNotFound_thenReturnNotFoundException() {
        when(bookingRepository.getBooking(booking.getId())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.getBooking(user.getId(), booking.getId()));

        assertEquals("Booking with Id=" + booking.getId() + " does not exist",
                notFoundException.getMessage());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void getBooking_whenBookingIsExist_thenReturnBooking() {
        Integer bookingId = booking.getId();
        when(bookingRepository.getBooking(bookingId)).thenReturn(Optional.of(booking));

        Booking returnedBooking = bookingServiceImpl.getBooking(booking.getBooker().getId(), bookingId);

        assertEquals(booking, returnedBooking);
        verify(bookingRepository, times(1)).getBooking(bookingId);
    }

    @Test
    void getAllUserBookings_whenUserNotFound_thenThrowException() {
        when(userRepository.findById(9999)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.getAllUserBookings(9999, "text", 0, 1));

        assertEquals("User with Id=" + 9999 + " - does not exist", exception.getMessage());
        verify(bookingRepository, never()).getAllUserBookings(requester.getId(),
                PageRequest.of(0, 1));
    }

    @Test
    void getAllUserBookings_whenRequestPaginationIsNotValid_thenThrowException() {
        assertThrows(RequestPaginationException.class, () -> bookingServiceImpl.getAllUserBookings(
                requester.getId(), State.ALL.toString(), -1, -1));
        verify(bookingRepository, never()).getAllUserBookings(requester.getId(),
                PageRequest.of(0, 1));
    }

    @Test
    void getAllUserBookings_whenStateIsNotCorrespond_thenThrowException() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));

        assertThrows(BookingErrorException.class, () -> bookingServiceImpl.getAllUserBookings(
                requester.getId(), "wrongState", 0, 1));

        verify(bookingRepository, never()).getAllUserBookings(requester.getId(),
                PageRequest.of(0, 1));
    }

    @Test
    void getAllUserBookings_whenStateIsWAITINGAndPageRequestIs01_thenReturnList() {
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getUserBookingsByStatus(requesterId, Status.WAITING, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllUserBookings(
                requesterId, State.WAITING.toString(), 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getUserBookingsByStatus(
                requesterId, Status.WAITING, PageRequest.of(0, 1));
    }

    @Test
    void getAllUserBookings_whenStateIsWAITINGAndThereIsNoPageRequest_thenReturnList() {
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getUserBookingsByStatus(requesterId, Status.WAITING))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllUserBookings(requesterId,
                State.WAITING.toString(), null, null);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getUserBookingsByStatus(requesterId, Status.WAITING);
    }

    @Test
    void getAllUserBookings_whenStateIsREJECTEDAndPageRequestIs01_thenReturnList() {
        booking.setStatus(Status.REJECTED);
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getUserBookingsByStatus(requesterId, Status.REJECTED, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllUserBookings(
                requesterId, State.REJECTED.toString(), 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getUserBookingsByStatus(
                requesterId, Status.REJECTED, PageRequest.of(0, 1));
    }

    @Test
    void getAllUserBookings_whenStateIsREJECTEDAndThereIsNoPageRequest_thenReturnList() {
        booking.setStatus(Status.REJECTED);
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getUserBookingsByStatus(requesterId, Status.REJECTED))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllUserBookings(requesterId,
                State.REJECTED.toString(), null, null);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getUserBookingsByStatus(requesterId, Status.REJECTED);
    }

    @Test
    void getAllUserBookings_whenStateIsFUTUREAndPageRequestIs01_thenReturnList() {
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getUserBookingInFuture(requesterId, now, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllUserBookings(
                requesterId, State.FUTURE.toString(), 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getUserBookingInFuture(
                requesterId, now, PageRequest.of(0, 1));
    }

    @Test
    void getAllUserBookings_whenStateIsFUTUREAndThereIsNoPageRequest_thenReturnList() {
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getUserBookingInFuture(requesterId, now))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllUserBookings(
                requesterId, State.FUTURE.toString(), null, null);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getUserBookingInFuture(
                requesterId, now);
    }

    @Test
    void getAllUserBookings_whenStateIsPASTAndPageRequestIs01_thenReturnList() {
        booking.setStart(now.minusHours(2));
        booking.setEnd(now.minusHours(1));
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getUserBookingInPast(requesterId, now, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllUserBookings(
                requesterId, State.PAST.toString(), 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getUserBookingInPast(
                requesterId, now, PageRequest.of(0, 1));
    }

    @Test
    void getAllUserBookings_whenStateIsPASTAndThereIsNoPageRequest_thenReturnList() {
        booking.setStart(now.minusHours(2));
        booking.setEnd(now.minusHours(1));
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getUserBookingInPast(requesterId, now))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllUserBookings(
                requesterId, State.PAST.toString(), null, null);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getUserBookingInPast(
                requesterId, now);
    }

    @Test
    void getAllUserBookings_whenStateIsCURRENTAndPageRequestIs01_thenReturnList() {
        booking.setStart(now.minusHours(2));
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getUserBookingInCurrent(requesterId, now, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllUserBookings(
                requesterId, State.CURRENT.toString(), 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getUserBookingInCurrent(
                requesterId, now, PageRequest.of(0, 1));
    }

    @Test
    void getAllUserBookings_whenStateIsCURRENTAndThereIsNoPageRequest_thenReturnList() {
        booking.setStart(now.minusHours(2));
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getUserBookingInCurrent(requesterId, now))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllUserBookings(
                requesterId, State.CURRENT.toString(), null, null);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getUserBookingInCurrent(
                requesterId, now);
    }

    @Test
    void getAllUserBookings_whenStateIsALLAndPageRequestIs01_thenReturnList() {
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getAllUserBookings(requesterId, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllUserBookings(
                requesterId, State.ALL.toString(), 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getAllUserBookings(
                requesterId, PageRequest.of(0, 1));
    }

    @Test
    void getAllUserBookings_whenStateIsALLAndThereIsNoPageRequest_thenReturnList() {
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getAllUserBookings(requesterId))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllUserBookings(requesterId,
                State.ALL.toString(), null, null);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getAllUserBookings(requesterId);
    }

    @Test
    void getAllOwnerBookings_whenUserNotFound_thenThrowException() {
        when(userRepository.findById(9999)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.getAllOwnerBooking(9999, "text", 0, 1));

        assertEquals("User with Id=" + 9999 + " - does not exist", exception.getMessage());
        verify(bookingRepository, never()).getAllOwnerBookings(requester.getId(),
                PageRequest.of(0, 1));
    }

    @Test
    void getAllOwnerBookings_whenRequestPaginationIsNotValid_thenThrowException() {
        assertThrows(RequestPaginationException.class, () -> bookingServiceImpl.getAllOwnerBooking(
                requester.getId(), State.ALL.toString(), -1, -1));
        verify(bookingRepository, never()).getAllOwnerBookings(requester.getId(),
                PageRequest.of(0, 1));
    }

    @Test
    void getAllOwnerBookings_whenStateIsNotCorrespond_thenThrowException() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));

        assertThrows(BookingErrorException.class, () -> bookingServiceImpl.getAllOwnerBooking(
                requester.getId(), "wrongState", 0, 1));

        verify(bookingRepository, never()).getAllOwnerBookings(requester.getId(),
                PageRequest.of(0, 1));
    }

    @Test
    void getAllOwnerBookings_whenStateIsWAITINGAndPageRequestIs01_thenReturnList() {
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getOwnerBookingsByStatus(requesterId, Status.WAITING, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllOwnerBooking(
                requesterId, State.WAITING.toString(), 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getOwnerBookingsByStatus(
                requesterId, Status.WAITING, PageRequest.of(0, 1));
    }

    @Test
    void getAllOwnerBookings_whenStateIsWAITINGAndThereIsNoPageRequest_thenReturnList() {
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getOwnerBookingsByStatus(requesterId, Status.WAITING))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllOwnerBooking(requesterId,
                State.WAITING.toString(), null, null);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getOwnerBookingsByStatus(requesterId, Status.WAITING);
    }

    @Test
    void getAllOwnerBookings_whenStateIsREJECTEDAndPageRequestIs01_thenReturnList() {
        booking.setStatus(Status.REJECTED);
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getOwnerBookingsByStatus(requesterId, Status.REJECTED, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllOwnerBooking(
                requesterId, State.REJECTED.toString(), 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getOwnerBookingsByStatus(
                requesterId, Status.REJECTED, PageRequest.of(0, 1));
    }

    @Test
    void getAllOwnerBookings_whenStateIsREJECTEDAndThereIsNoPageRequest_thenReturnList() {
        booking.setStatus(Status.REJECTED);
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getOwnerBookingsByStatus(requesterId, Status.REJECTED))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllOwnerBooking(requesterId,
                State.REJECTED.toString(), null, null);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getOwnerBookingsByStatus(requesterId, Status.REJECTED);
    }

    @Test
    void getAllOwnerBookings_whenStateIsFUTUREAndPageRequestIs01_thenReturnList() {
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getOwnerBookingInFuture(requesterId, now, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllOwnerBooking(
                requesterId, State.FUTURE.toString(), 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getOwnerBookingInFuture(
                requesterId, now, PageRequest.of(0, 1));
    }

    @Test
    void getAllOwnerBookings_whenStateIsFUTUREAndThereIsNoPageRequest_thenReturnList() {
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getOwnerBookingInFuture(requesterId, now))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllOwnerBooking(
                requesterId, State.FUTURE.toString(), null, null);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getOwnerBookingInFuture(
                requesterId, now);
    }

    @Test
    void getAllOwnerBookings_whenStateIsPASTAndPageRequestIs01_thenReturnList() {
        booking.setStart(now.minusHours(2));
        booking.setEnd(now.minusHours(1));
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getOwnerBookingInPast(requesterId, now, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllOwnerBooking(
                requesterId, State.PAST.toString(), 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getOwnerBookingInPast(
                requesterId, now, PageRequest.of(0, 1));
    }

    @Test
    void getAllOwnerBookings_whenStateIsPASTAndThereIsNoPageRequest_thenReturnList() {
        booking.setStart(now.minusHours(2));
        booking.setEnd(now.minusHours(1));
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getOwnerBookingInPast(requesterId, now))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllOwnerBooking(
                requesterId, State.PAST.toString(), null, null);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getOwnerBookingInPast(
                requesterId, now);
    }

    @Test
    void getAllOwnerBookings_whenStateIsCURRENTAndPageRequestIs01_thenReturnList() {
        booking.setStart(now.minusHours(2));
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getOwnerBookingInCurrent(requesterId, now, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllOwnerBooking(
                requesterId, State.CURRENT.toString(), 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getOwnerBookingInCurrent(
                requesterId, now, PageRequest.of(0, 1));
    }

    @Test
    void getAllOwnerBookings_whenStateIsCURRENTAndThereIsNoPageRequest_thenReturnList() {
        booking.setStart(now.minusHours(2));
        Integer requesterId = requester.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(bookingRepository.getOwnerBookingInCurrent(requesterId, now))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllOwnerBooking(
                requesterId, State.CURRENT.toString(), null, null);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(bookingRepository, times(1)).getOwnerBookingInCurrent(
                requesterId, now);
    }

    @Test
    void getAllOwnerBooking_whenStateIsALLAndPageRequestIs01_thenReturnList() {
        Integer ownerId = user.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(bookingRepository.getAllOwnerBookings(ownerId, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllOwnerBooking(
                ownerId, State.ALL.toString(), 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, times(1)).getAllOwnerBookings(
                ownerId, PageRequest.of(0, 1));
    }

    @Test
    void getAllOwnerBooking_whenStateIsALLAndThereIsNoPageRequest_thenReturnList() {
        Integer ownerId = user.getId();
        when(clock.getZone()).thenReturn(NOW_ZDT.getZone());
        when(clock.instant()).thenReturn(NOW_ZDT.toInstant());
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(bookingRepository.getAllOwnerBookings(ownerId))
                .thenReturn(List.of(booking));

        List<Booking> returnedList = bookingServiceImpl.getAllOwnerBooking(
                ownerId, State.ALL.toString(), null, null);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, times(1)).getAllOwnerBookings(
                ownerId);
    }
}