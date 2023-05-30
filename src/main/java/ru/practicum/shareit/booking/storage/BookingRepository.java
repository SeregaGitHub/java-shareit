package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.bookingUtil.Status;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    String SELECT_BOOKING = "select b from Booking as b " +
                            "JOIN FETCH b.item as i " +
                            "JOIN FETCH i.owner as o " +
                            "JOIN FETCH b.booker as u ";

    @Query(
            SELECT_BOOKING + "where b.id = ?1"
    )
    Optional<Booking> getBooking(Integer bookingId);

    @Query(
            SELECT_BOOKING + "where u.id = ?1 order by b.start desc"
    )
    List<Booking> getAllUserBookings(Integer booker);

    @Query(
            SELECT_BOOKING + "where u.id = ?1 and b.status = ?2"
    )
    List<Booking> getUserBookingsByStatus(Integer booker, Status status);

    @Query(
            SELECT_BOOKING + "where u.id = ?1 and b.start > ?2 order by b.start desc"
    )
    List<Booking> getUserBookingInFuture(Integer booker, LocalDateTime localDateTimeNow);

    @Query(
            SELECT_BOOKING + "where u.id = ?1 and b.end < ?2 order by b.start desc"
    )
    List<Booking> getUserBookingInPast(Integer booker, LocalDateTime localDateTimeNow);

    @Query(
            SELECT_BOOKING + "where u.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start desc"
    )
    List<Booking> getUserBookingInCurrent(Integer booker, LocalDateTime localDateTimeNow);

    @Query(
            SELECT_BOOKING + "where o.id = ?1 order by b.start desc"
    )
    List<Booking> getAllOwnerBookings(Integer owner);

    @Query(
            SELECT_BOOKING + "where o.id = ?1 and b.status = ?2"
    )
    List<Booking> getOwnerBookingsByStatus(Integer booker, Status status);

    @Query(
            SELECT_BOOKING + "where o.id = ?1 and b.start > ?2 order by b.start desc"
    )
    List<Booking> getOwnerBookingInFuture(Integer booker, LocalDateTime localDateTimeNow);

    @Query(
            SELECT_BOOKING + "where o.id = ?1 and b.end < ?2 order by b.start desc"
    )
    List<Booking> getOwnerBookingInPast(Integer booker, LocalDateTime localDateTimeNow);

    @Query(
            SELECT_BOOKING + "where o.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start desc"
    )
    List<Booking> getOwnerBookingInCurrent(Integer booker, LocalDateTime localDateTimeNow);

    @Query(nativeQuery = true)
    List<BookingForItemDto> getLastAndNextBooking(Integer id, LocalDateTime now);

    @Query(
            value = "select new ru.practicum.shareit.booking.dto.BookingForItemDto(b.id, b.start, " +
                    "b.end, i.id, u.id) " +
                    "from Booking as b " +
                    "JOIN b.booker as u " +
                    "JOIN b.item as i " +
                    "where u.id = ?1 and i.id = ?2 and b.status like ?3 " +
                    "order by b.end"
    )
    List<BookingForItemDto> getBookingDtoByBooker_IdAndItem_Id(Integer userId, Integer itemId, Status status, PageRequest pageRequest);
}
