package ru.practicum.shareit.item.itemUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemUtilTest {
    private User user;
    private Item item;
    private ItemDto toUpdateItemDto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        user = new User(0, "name", "user@yandex.ru");
        User requester = new User(1, "requesterName", "requester@yandex.ru");
        toUpdateItemDto = new ItemDto(0, "updatedItemName", "updatedItemDescription", false);
        ItemRequest itemRequest = new ItemRequest(0, "itemN", now, requester);
        ItemWithRequestDto itemWithRequestDto = new ItemWithRequestDto(0, "itemName", "itemDescription", true,
                requester.getId());
        item = ItemMapper.toItem(user, itemWithRequestDto, itemRequest);
    }

    @Test
    void makeItem_whenAllFieldsMustBeUpdated_whenReturnUpdatedItem() {
        Item returnedItem = ItemUtil.makeItem(item, toUpdateItemDto);

        assertEquals(0, returnedItem.getId());
        assertEquals("updatedItemName", returnedItem.getName());
        assertEquals("updatedItemDescription", returnedItem.getDescription());
        assertFalse(returnedItem.getAvailable());
        assertEquals(user, returnedItem.getOwner());
    }

    @Test
    void makeItem_whenPartOfTheFieldsMustBeUpdated_whenReturnUpdatedItem() {
        ItemDto toUpdateItemDto = ItemDto.builder()
                .id(0)
                .description("updatedItemDescription")
                .build();
        Item returnedItem = ItemUtil.makeItem(item, toUpdateItemDto);

        assertEquals(0, returnedItem.getId());
        assertEquals("itemName", returnedItem.getName());
        assertEquals("updatedItemDescription", returnedItem.getDescription());
        assertTrue(returnedItem.getAvailable());
        assertEquals(user, returnedItem.getOwner());
    }
}