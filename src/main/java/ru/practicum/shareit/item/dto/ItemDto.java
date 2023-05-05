package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.mark.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {
    private Integer id;
    @NotBlank(groups = {Create.class}, message = "Имя не может быть пустым.")
    @Size(max = 64, groups = {Create.class}, message = "Имя не может быть больше 64 символов.")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Описание не может быть пустым.")
    @Size(max = 1024, groups = {Create.class}, message = "Описание не может быть больше 1024 символов.")
    private String description;
    @NotNull(groups = {Create.class}, message = "Пользователь должен знать - доступна ли вещь для аренды.")
    @JsonProperty(value = "available")
    private Boolean available;
}
