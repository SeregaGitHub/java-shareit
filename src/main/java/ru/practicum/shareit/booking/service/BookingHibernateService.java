package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.bookingUtil.BookingUtil;
import ru.practicum.shareit.booking.bookingUtil.State;
import ru.practicum.shareit.booking.bookingUtil.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.bookingUtil.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BookingErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingHibernateService implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking addBooking(Integer booker, BookingDto bookingDto) {
        Booking booking;
        boolean checkTime = BookingUtil.checkStartAndEndTime(bookingDto.getStart(), bookingDto.getEnd());
        if (checkTime) {
            User user = userRepository.findById(booker).orElseThrow(
                    () -> new NotFoundException("User with Id=" + booker + " - does not exist"));
            Item item = itemRepository.findItemByIdWithOwner(bookingDto.getItemId()).orElseThrow(
                    () -> new NotFoundException("Item with Id=" + bookingDto.getItemId() + " - does not exist"));
            BookingUtil.checkOwner(booker, item.getOwner().getId());
            if (!item.getAvailable()) {
                throw new BookingErrorException("Item is not available");
            }
            booking = BookingMapper.toBooking(user, bookingDto, item);
            log.info("Booking item with Id={} was done", bookingDto.getItemId());
        } else {
            throw new BookingErrorException("Start time cannot be after end time or be equals him");
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking setNewStatus(Integer owner, Integer bookingId, Boolean approved) {
        Booking booking = bookingRepository.getBooking(bookingId).orElseThrow(
                () -> new NotFoundException("Booking with Id=" + bookingId + " does not exist"));
        if (!Objects.equals(booking.getItem().getOwner().getId(), owner)) {
            throw new NotFoundException("This action is prohibited for a user with Id=" + owner);
        }
        if (booking.getStatus().equals(Status.WAITING)) {
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        } else {
            throw new BookingErrorException("New status has already been confirmed");
        }
        bookingRepository.save(booking);
        log.info("Booking with Id={} have new status. Approved - {}", bookingId, approved);
        return booking;
    }

    @Override
    public Booking getBooking(Integer userId, Integer bookingId) {
        Booking booking = bookingRepository.getBooking(bookingId).orElseThrow(
                () -> new NotFoundException("Booking with Id=" + bookingId + " does not exist"));
        if (Objects.equals(booking.getBooker().getId(), userId) ||
                Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            return booking;
        } else {
            throw new NotFoundException("User with Id=" + userId + " - can not get this booking");
        }
    }

    @Override
    public List<Booking> getAllUserBookings(Integer booker, String stateString) {
        userRepository.findById(booker).orElseThrow(
                () -> new NotFoundException("User with Id=" + booker + " - does not exist"));
        State state = BookingUtil.makeState(stateString);
        LocalDateTime localDateTimeNow = LocalDateTime.now();

        if (state.equals(State.WAITING)) {
            return bookingRepository.getUserBookingsByStatus(booker, Status.WAITING);
        } else if (state.equals(State.REJECTED)) {
            return bookingRepository.getUserBookingsByStatus(booker, Status.REJECTED);
        } else if (state.equals(State.FUTURE)) {
            return bookingRepository.getUserBookingInFuture(booker, localDateTimeNow);
        } else if (state.equals(State.PAST)) {
            return bookingRepository.getUserBookingInPast(booker, localDateTimeNow);
        } else if (state.equals(State.CURRENT)) {
            return bookingRepository.getUserBookingInCurrent(booker, localDateTimeNow);
        } else {
            return bookingRepository.getAllUserBookings(booker);
        }
    }

    @Override
    public List<Booking> getAllOwnerBooking(Integer owner, String stateString) {
        userRepository.findById(owner).orElseThrow(
                () -> new NotFoundException("User with Id=" + owner + " - does not exist"));
        State state = BookingUtil.makeState(stateString);
        LocalDateTime localDateTimeNow = LocalDateTime.now();

        if (state.equals(State.WAITING)) {
            return bookingRepository.getOwnerBookingsByStatus(owner, Status.WAITING);
        } else if (state.equals(State.REJECTED)) {
            return bookingRepository.getOwnerBookingsByStatus(owner, Status.REJECTED);
        } else if (state.equals(State.FUTURE)) {
            return bookingRepository.getOwnerBookingInFuture(owner, localDateTimeNow);
        } else if (state.equals(State.PAST)) {
            return bookingRepository.getOwnerBookingInPast(owner, localDateTimeNow);
        } else if (state.equals(State.CURRENT)) {
            return bookingRepository.getOwnerBookingInCurrent(owner, localDateTimeNow);
        } else {
            return bookingRepository.getAllOwnerBookings(owner);
        }
    }
}
