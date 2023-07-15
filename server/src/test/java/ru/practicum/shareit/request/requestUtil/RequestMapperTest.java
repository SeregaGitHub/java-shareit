package ru.practicum.shareit.request.requestUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;
import ru.practicum.shareit.item.itemUtil.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestMapperTest {
    private User user;
    private User requester;
    private Item item;
    private ItemDto itemDto;
    private ItemWithRequestDto itemWithRequestDto;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemWithRequestIdDto itemWithRequestIdDto;
    private ItemRequestWithItemsDto itemRequestWithItemsDto;
    private CommentDto commentDto;
    private LocalDateTime now;
    private final RequestMapper requestMapper = new RequestMapper();

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        user = new User(0, "name", "user@yandex.ru");
        requester = new User(1, "requesterName", "requester@yandex.ru");
        itemDto = new ItemDto(0, "itemName", "itemDescription", true);
        commentDto = new CommentDto(0, "text", "name", now);
        itemRequest = new ItemRequest(0, "itemN", now, requester);
        itemRequestDto = new ItemRequestDto(0, "itemN", now, requester.getId());
        itemWithRequestDto = new ItemWithRequestDto(0, "itemName", "itemDescription", true,
                requester.getId());
        itemWithRequestIdDto = new ItemWithRequestIdDto(0, "itemName", "itemDescription",
                true, itemRequest.getId());
        itemRequestWithItemsDto = new ItemRequestWithItemsDto(itemRequest.getId(), "itemN",
                now, requester.getId(), List.of(itemWithRequestIdDto));
        item = ItemMapper.toItem(user, itemWithRequestDto, itemRequest);
    }

    @Test
    void toItemRequest() {
        ItemRequest itemRequestCheck = requestMapper.toItemRequest(itemRequestDto, requester);

        assertEquals(itemRequest, itemRequestCheck);
    }

    @Test
    void toItemRequestDto() {
        ItemRequestDto itemRequestDtoCheck = requestMapper.toItemRequestDto(itemRequest);

        assertEquals(itemRequestDto, itemRequestDtoCheck);
    }

    @Test
    void toItemRequestWithItemsDto() {
        ItemRequestWithItemsDto itemRequestWithItemsDtoCheck = requestMapper.toItemRequestWithItemsDto(
                itemRequestDto, List.of(itemWithRequestIdDto)
        );

        assertEquals(itemRequestWithItemsDto, itemRequestWithItemsDtoCheck);
    }
}