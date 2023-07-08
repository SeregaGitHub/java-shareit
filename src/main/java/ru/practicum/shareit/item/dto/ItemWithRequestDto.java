package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.util.mark.Create;
import ru.practicum.shareit.util.mark.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Objects;

public class ItemWithRequestDto extends ItemDto {
    private Integer requestId;

    public ItemWithRequestDto(Integer id,
                       @NotBlank(groups = {Create.class}, message = "Имя не может быть пустым.")
                       @Size(max = 64, groups = {Create.class, Update.class}, message = "Имя не может быть больше 64 символов.")
                       String name,
                       @NotBlank(groups = {Create.class}, message = "Описание не может быть пустым.")
                       @Size(max = 1024, groups = {Create.class, Update.class}, message = "Описание не может быть больше 1024 символов.")
                       String description,
                       @NotNull(groups = {Create.class}, message = "Пользователь должен знать - доступна ли вещь для аренды.")
                       Boolean available,
                       @Positive(groups = {Create.class}, message = "Идентификатором запроса вещи - должно быть положительное число")
                       Integer requestId) {
        super(id, name, description, available);
        this.requestId = requestId;
    }

    public Integer getRequestId() {
        return requestId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ItemWithRequestDto that = (ItemWithRequestDto) o;
        return Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requestId);
    }
}
