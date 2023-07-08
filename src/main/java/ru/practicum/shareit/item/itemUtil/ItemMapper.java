package ru.practicum.shareit.item.itemUtil;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item toItem(User owner, ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public Item toItem(User owner, ItemWithRequestDto itemDto, ItemRequest itemRequest) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .itemRequest(itemRequest)
                .build();
    }

    public ItemWithBookingDto toItemWithBookingAndCommentDto(Item item, List<BookingForItemDto> bookingList,
                                                                    LocalDateTime now, List<CommentDto> commentDtoList) {
        BookingForItemDto lastBooking = null;
        BookingForItemDto nextBooking = null;

        for (BookingForItemDto b : bookingList) {
            if (b.getStartTime().isBefore(now)) {
                lastBooking = b;
            } else if (b.getStartTime().isAfter(now)) {
                nextBooking = b;
            }
        }
        return new ItemWithBookingDto(item.getId(),
                item.getName(), item.getDescription(), item.getAvailable(), lastBooking, nextBooking, commentDtoList);
    }

    public ItemWithBookingDto toItemWithBookingDto(ItemShot itemShot, List<BookingForItemDto> bookingList,
                                                          LocalDateTime now) {
        BookingForItemDto lastBooking = null;
        BookingForItemDto nextBooking = null;

        for (BookingForItemDto b: bookingList) {
            if (b.getStartTime().isBefore(now)) {
                lastBooking = b;
            } else if (b.getStartTime().isAfter(now)) {
                nextBooking = b;
            }
        }
        return new ItemWithBookingDto(itemShot.getId(), itemShot.getName(), itemShot.getDescription(),
                itemShot.getAvailable(), lastBooking, nextBooking, new ArrayList<>());
    }

    public ItemWithBookingDto toItemNoBookingDto(Item item, List<CommentDto> commentDtoList) {
        return new ItemWithBookingDto(item.getId(),
                item.getName(), item.getDescription(), item.getAvailable(), null, null, commentDtoList);
    }
}
