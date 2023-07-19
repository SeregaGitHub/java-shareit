package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.mark.Create;
import ru.practicum.shareit.util.mark.Update;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	private Integer id;
	@NotNull(groups = Create.class, message = "Вам необходимо указать начало бронирования")
	@FutureOrPresent(groups = Create.class, message = "Начало бронирования не должно быть раньше настоящего времени")
	private LocalDateTime start;
	@NotNull(groups = Create.class, message = "Вам необходимо указать окончание бронирования")
	@Future(groups = {Create.class, Update.class}, message = "Окончание бронирования должно быть позже настоящего времени")
	private LocalDateTime end;
	private int itemId;
}
