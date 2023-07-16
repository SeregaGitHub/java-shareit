package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.mark.Create;
import ru.practicum.shareit.util.mark.Update;

import javax.validation.constraints.*;

@Data
@Builder
public class UserDto {
    @Positive(groups = Update.class, message = "Id пользователя не может быть отрицательным числом.")
    private Integer id;
    @NotBlank(groups = {Create.class}, message = "Имя не может быть пустым.")
    @Size(max = 64, groups = {Create.class, Update.class}, message = "Имя не может быть больше 64 символов.")
    private String name;
    @NotNull(groups = {Create.class}, message = "У пользователя должен быть email.")
    @Email(groups = {Create.class, Update.class}, message = "Ваш email не валиден.")
    private String email;
}
