package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.util.List;
import java.util.Objects;

public class ItemWithBookingDto extends ItemDto {
    private final BookingForItemDto lastBooking;
    private final BookingForItemDto nextBooking;
    private final List<CommentDto> comments;

    public ItemWithBookingDto(Integer id,
                              String name,
                              String description,
                              Boolean available,
                              BookingForItemDto lastBooking,
                              BookingForItemDto nextBooking,
                              List<CommentDto> comments
    ) {
                       super(id, name, description, available);
                       this.lastBooking = lastBooking;
                       this.nextBooking = nextBooking;
                       this.comments = comments;

    }

    public BookingForItemDto getLastBooking() {
        return lastBooking;
    }

    public BookingForItemDto getNextBooking() {
        return nextBooking;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ItemWithBookingDto that = (ItemWithBookingDto) o;
        return Objects.equals(lastBooking, that.lastBooking) && Objects.equals(nextBooking, that.nextBooking) && Objects.equals(comments, that.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lastBooking, nextBooking, comments);
    }
}
