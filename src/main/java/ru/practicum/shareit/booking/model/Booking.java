package ru.practicum.shareit.booking.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.bookingUtil.Status;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NamedNativeQuery(name = "Booking.getLastAndNextBooking",
                  query = "(SELECT b.BOOKING_ID as id, b.START_TIME as startTime, b.END_TIME as endTime, " +
                          "b.ITEM_ID as itemId, b.USER_ID as bookerId " +
                          "FROM BOOKING as b " +
                          "WHERE b.ITEM_ID = ?1 AND b.START_TIME < ?2  AND b.STATUS = 'APPROVED' " +
                          "ORDER BY b.START_TIME DESC LIMIT 1) " +
                          "UNION " +
                          "(SELECT b.BOOKING_ID as id, b.START_TIME as startTime, b.END_TIME as endTime, " +
                          "b.ITEM_ID as itemId, b.USER_ID as bookerId " +
                          "FROM BOOKING as b " +
                          "WHERE b.ITEM_ID = ?1 AND b.START_TIME > ?2  AND b.STATUS = 'APPROVED' " +
                          "ORDER BY b.START_TIME LIMIT 1) " +
                          "ORDER BY startTime",
                  resultSetMapping = "Mapping.BookingForItemDto")
@SqlResultSetMapping(name = "Mapping.BookingForItemDto",
                     classes = @ConstructorResult(targetClass = BookingForItemDto.class,
                                                  columns = {@ColumnResult(name = "id", type = Integer.class),
                                                             @ColumnResult(name = "startTime", type = LocalDateTime.class),
                                                             @ColumnResult(name = "endTime", type = LocalDateTime.class),
                                                             @ColumnResult(name = "itemId", type = Integer.class),
                                                             @ColumnResult(name = "bookerId", type = Integer.class)}))
@Entity
@Table(name = "booking", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @Column(name = "booking_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @NotNull
    @Column(name = "start_time", nullable = false)
    LocalDateTime start;
    @NotNull
    @Column(name = "end_time", nullable = false)
    LocalDateTime end;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    Item item;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User booker;
    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    Status status;
}
