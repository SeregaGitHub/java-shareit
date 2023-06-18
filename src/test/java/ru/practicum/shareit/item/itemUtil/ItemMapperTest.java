package ru.practicum.shareit.item.itemUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {
    private User user;
    private Item item;
    private ItemDto itemDto;
    private ItemWithRequestDto itemWithRequestDto;
    private ItemWithBookingDto itemWithBookingDto;
    private ItemRequest itemRequest;
    private CommentDto commentDto;
    private LocalDateTime now;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        user = new User(0, "name", "user@yandex.ru");
        User requester = new User(1, "requesterName", "requester@yandex.ru");
        itemDto = new ItemDto(0, "itemName", "itemDescription", true);
        commentDto = new CommentDto(0, "text", "name", now);
        itemRequest = new ItemRequest(0, "itemN", now, requester);
        itemWithRequestDto = new ItemWithRequestDto(0, "itemName", "itemDescription", true,
                requester.getId());
        itemWithBookingDto = new ItemWithBookingDto(0, "itemName", "itemDescription", true,
                null, null, List.of(commentDto));
        item = ItemMapper.toItem(user, itemWithRequestDto, itemRequest);
        BookingForItemDto bookingForItemDto = BookingForItemDto.builder()
                .id(1)
                .startTime(now.plusHours(1))
                .endTime(now.plusHours(2))
                .itemId(item.getId())
                .bookerId(requester.getId())
                .build();
    }

    @Test
    void toItemDto() {
        ItemDto itemDtoCheck = ItemMapper.toItemDto(item);

        assertEquals(itemDto, itemDtoCheck);
    }

    @Test
    void toItem() {
        Item itemCheck = ItemMapper.toItem(user, itemWithRequestDto, itemRequest);

        assertEquals(item, itemCheck);
    }

    @Test
    void toItemWithBookingAndCommentDto() {
        ItemWithBookingDto itemWithBookingDtoCheck = ItemMapper.toItemWithBookingAndCommentDto(
                item, new ArrayList<>(), now, List.of(commentDto)
        );

        assertEquals(itemWithBookingDto, itemWithBookingDtoCheck);
    }

    @Test
    void toItemWithBookingDto() {
        ItemShot itemShot = new ItemShot() {
            @Override
            public Integer getId() {
                return item.getId();
            }

            @Override
            public String getName() {
                return item.getName();
            }

            @Override
            public String getDescription() {
                return item.getDescription();
            }

            @Override
            public Boolean getAvailable() {
                return item.getAvailable();
            }
        };

        ItemWithBookingDto dto = new ItemWithBookingDto(0, "itemName", "itemDescription", true,
                null, null, new ArrayList<>());

        ItemWithBookingDto itemWithBookingDtoCheck = ItemMapper.toItemWithBookingDto(
                itemShot, new ArrayList<>(), now);

        assertEquals(dto, itemWithBookingDtoCheck);
    }

    @Test
    void toItemNoBookingDto() {
        ItemWithBookingDto itemWithNoBookingDto = ItemMapper.toItemNoBookingDto(
                item, List.of(commentDto)
        );

        assertEquals(itemWithBookingDto, itemWithNoBookingDto);
    }
}