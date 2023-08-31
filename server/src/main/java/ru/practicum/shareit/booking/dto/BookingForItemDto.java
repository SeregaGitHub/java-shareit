package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingForItemDto {
    private Integer id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer itemId;
    private Integer bookerId;
}
