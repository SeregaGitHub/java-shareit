package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.bookingUtil.Status;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryTest {
    private User booker;
    private Item item;
    private LocalDateTime now;
    private Booking futureBooking;
    private Booking pastBooking;
    private Booking currentBooking;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        User user = User.builder()
                .name("name")
                .email("user@yandex.ru")
                .build();
        booker = User.builder()
                .name("requesterName")
                .email("requester@yandex.ru")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .description("itemN")
                .created(now)
                .requester(booker)
                .build();
        item = Item.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .itemRequest(itemRequest)
                .build();
        futureBooking = Booking.builder()
                .start(now.plusHours(2))
                .end(now.plusHours(3))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
        pastBooking = Booking.builder()
                .start(now.minusHours(3))
                .end(now.minusHours(2))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
        currentBooking = Booking.builder()
                .start(now.minusHours(1))
                .end(now.plusHours(1))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        userRepository.save(user);
        userRepository.save(booker);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item);
        bookingRepository.save(futureBooking);
        bookingRepository.save(pastBooking);
        bookingRepository.save(currentBooking);
    }

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getBooking() {
        Optional<Booking> returnedBooking = bookingRepository.getBooking(futureBooking.getId());

        assertTrue(returnedBooking.isPresent());
    }

    @Test
    void getAllUserBookings_whenThereIsNoPageRequest_thenReturnAllBookings() {
        List<Booking> returnedList = bookingRepository.getAllUserBookings(booker.getId());

        assertEquals(3, returnedList.size());
    }

    @Test
    void getAllUserBookings_whenSizeIsTwo_whenReturnListOfTwoBookings() {
        List<Booking> returnedList = bookingRepository.getAllUserBookings(booker.getId(), PageRequest.of(0, 2));

        assertEquals(2, returnedList.size());
    }

    @Test
    void getUserBookingsByStatus_whenThereIsNoPageRequestAndStatusIsWAITING_thenReturnListOfTwoBookings() {
        pastBooking.setStatus(Status.APPROVED);
        List<Booking> returnedList = bookingRepository.getUserBookingsByStatus(booker.getId(), Status.WAITING);

        assertEquals(2, returnedList.size());
    }

    @Test
    void getUserBookingsByStatus_whenSizeIsOneAndStatusIsWAITING_thenReturnListOfOneBookings() {
        pastBooking.setStatus(Status.APPROVED);
        List<Booking> returnedList = bookingRepository.getUserBookingsByStatus(booker.getId(), Status.WAITING,
                PageRequest.of(0, 1));

        assertEquals(1, returnedList.size());
    }

    @Test
    void getUserBookingInFuture_whenThereIsNoPageRequest_thenReturnListOfOneBookings() {
        List<Booking> returnedList = bookingRepository.getUserBookingInFuture(booker.getId(), now);

        assertEquals(1, returnedList.size());
    }

    @Test
    void getUserBookingInFuture_whenSizeIsOne_thenReturnListOfOneBookings() {
        List<Booking> returnedList = bookingRepository.getUserBookingInFuture(booker.getId(), now,
                PageRequest.of(0, 1));

        assertEquals(1, returnedList.size());
    }

    @Test
    void getUserBookingInPast_whenThereIsNoPageRequest_thenReturnListOfOneBookings() {
        List<Booking> returnedList = bookingRepository.getUserBookingInPast(booker.getId(), now);

        assertEquals(1, returnedList.size());
    }

    @Test
    void getUserBookingInPast_whenSizeIsOne_thenReturnListOfOneBookings() {
        List<Booking> returnedList = bookingRepository.getUserBookingInPast(booker.getId(), now,
                PageRequest.of(0, 1));

        assertEquals(1, returnedList.size());
    }

    @Test
    void getUserBookingInCurrent_whenThereIsNoPageRequest_thenReturnListOfOneBookings() {
        List<Booking> returnedList = bookingRepository.getUserBookingInCurrent(booker.getId(), now);

        assertEquals(1, returnedList.size());
    }

    @Test
    void getUserBookingInCurrent_whenSizeIsOne_thenReturnListOfOneBookings() {
        List<Booking> returnedList = bookingRepository.getUserBookingInCurrent(booker.getId(), now,
                PageRequest.of(0, 1));

        assertEquals(1, returnedList.size());
    }

    @Test
    void getAllOwnerBookings_whenThereIsNoPageRequest_thenReturnAllBookings() {
        List<Booking> returnedList = bookingRepository.getAllOwnerBookings(item.getOwner().getId());

        assertEquals(3, returnedList.size());
    }

    @Test
    void getAllOwnerBookings_whenSizeIsTwo_whenReturnListOfTwoBookings() {
        List<Booking> returnedList = bookingRepository.getAllOwnerBookings(item.getOwner().getId(), PageRequest.of(0, 2));

        assertEquals(2, returnedList.size());
    }

    @Test
    void getOwnerBookingsByStatus_whenThereIsNoPageRequestAndStatusIsWAITING_thenReturnListOfTwoBookings() {
        pastBooking.setStatus(Status.APPROVED);
        List<Booking> returnedList = bookingRepository.getOwnerBookingsByStatus(item.getOwner().getId(), Status.WAITING);

        assertEquals(2, returnedList.size());
    }

    @Test
    void getOwnerBookingsByStatus_whenSizeIsOneAndStatusIsWAITING_thenReturnListOfOneBookings() {
        pastBooking.setStatus(Status.APPROVED);
        List<Booking> returnedList = bookingRepository.getOwnerBookingsByStatus(item.getOwner().getId(), Status.WAITING,
                PageRequest.of(0, 1));

        assertEquals(1, returnedList.size());
    }

    @Test
    void getOwnerBookingInFuture_whenThereIsNoPageRequest_thenReturnListOfOneBookings() {
        List<Booking> returnedList = bookingRepository.getOwnerBookingInFuture(item.getOwner().getId(), now);

        assertEquals(1, returnedList.size());
    }

    @Test
    void getOwnerBookingInFuture_whenSizeIsOne_thenReturnListOfOneBookings() {
        List<Booking> returnedList = bookingRepository.getOwnerBookingInFuture(item.getOwner().getId(), now,
                PageRequest.of(0, 1));

        assertEquals(1, returnedList.size());
    }

    @Test
    void getOwnerBookingInPast_whenThereIsNoPageRequest_thenReturnListOfOneBookings() {
        List<Booking> returnedList = bookingRepository.getOwnerBookingInPast(item.getOwner().getId(), now);

        assertEquals(1, returnedList.size());
    }

    @Test
    void getOwnerBookingInPast_whenSizeIsOne_thenReturnListOfOneBookings() {
        List<Booking> returnedList = bookingRepository.getOwnerBookingInPast(item.getOwner().getId(), now,
                PageRequest.of(0, 1));

        assertEquals(1, returnedList.size());
    }

    @Test
    void getOwnerBookingInCurrent_whenThereIsNoPageRequest_thenReturnListOfOneBookings() {
        List<Booking> returnedList = bookingRepository.getOwnerBookingInCurrent(item.getOwner().getId(), now);

        assertEquals(1, returnedList.size());
    }

    @Test
    void getOwnerBookingInCurrent_whenSizeIsOne_thenReturnListOfOneBookings() {
        List<Booking> returnedList = bookingRepository.getOwnerBookingInCurrent(item.getOwner().getId(), now,
                PageRequest.of(0, 1));

        assertEquals(1, returnedList.size());
    }

    @Test
    void getLastAndNextBooking() {
        currentBooking.setStatus(Status.APPROVED);
        futureBooking.setStatus(Status.APPROVED);
        List<BookingForItemDto> returnedList = bookingRepository.getLastAndNextBooking(item.getId(), now);

        assertEquals(2, returnedList.size());
    }

    @Test
    void getBookingDtoByBooker_IdAndItem_Id_whenSizeIsTwo_thenReturnListOfTwoBookings() {
        List<BookingForItemDto> returnedList = bookingRepository.getBookingDtoByBooker_IdAndItem_Id(
                booker.getId(), item.getId(), Status.WAITING, PageRequest.of(0, 2));

        assertEquals(2, returnedList.size());
    }
}