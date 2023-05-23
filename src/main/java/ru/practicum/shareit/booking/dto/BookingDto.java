package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.mark.Create;
import ru.practicum.shareit.util.mark.Update;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Integer id;
    @NotNull(groups = Create.class, message = "Вам необходимо указать начало бронирования")
    @FutureOrPresent(groups = Create.class, message = "Начало бронирования не должно быть раньше настоящего времени")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;
    @NotNull(groups = Create.class, message = "Вам необходимо указать окончание бронирования")
    @Future(groups = {Create.class, Update.class}, message = "Окончание бронирования должно быть позже настоящего времени")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;
    @NotNull(groups = Create.class, message = "Вам необходимо указать какой предмет вы хотите забронировать")
    private Integer itemId;
}
