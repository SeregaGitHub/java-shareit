package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.mark.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserDto {
    private Integer id;
    @NotBlank(groups = {Create.class}, message = "Имя не может быть пустым.")
    @Size(max = 64, groups = {Create.class}, message = "Имя не может быть больше 64 символов.")
    private String name;
    @NotNull(groups = {Create.class}, message = "У пользователя должен быть email.")
    @Email(groups = {Create.class}, message = "Ваш email не валиден.")
    private String email;
}
