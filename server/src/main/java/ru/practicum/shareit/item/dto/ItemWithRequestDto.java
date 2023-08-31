package ru.practicum.shareit.item.dto;

import java.util.Objects;

public class ItemWithRequestDto extends ItemDto {
    private Integer requestId;

    public ItemWithRequestDto(Integer id,
                       String name,
                       String description,
                       Boolean available,
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
