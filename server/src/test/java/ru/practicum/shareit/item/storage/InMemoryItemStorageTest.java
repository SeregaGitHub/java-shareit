package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InMemoryItemStorageTest {
    @InjectMocks
    private InMemoryItemStorage inMemoryItemStorage;

    private User owner;
    private ItemDto itemDto;
    private ItemDto secondItemDto;

    @BeforeEach
    void beforeEach() {
        owner = new User(1, "ownerName", "owner@yandex.ru");
        itemDto = new ItemDto(0, "itemName", "itemDescription", true);
        secondItemDto = new ItemDto(0, "secondItemName", "secondItemDescription", true);
        inMemoryItemStorage.addItem(owner, itemDto);
    }

    @AfterEach
    void afterEach() {
        inMemoryItemStorage.deleteItem(itemDto.getId());
        ReflectionTestUtils.setField(inMemoryItemStorage, "itemId", 0);
        ReflectionTestUtils.invokeMethod(inMemoryItemStorage, "clearUserItems");
    }

    @Test
    void addItem() {
        ItemDto returnedItemDto = inMemoryItemStorage.addItem(owner, secondItemDto);

        assertEquals(2, returnedItemDto.getId());
        assertEquals("secondItemName", returnedItemDto.getName());
        assertEquals("secondItemDescription", returnedItemDto.getDescription());
        assertTrue(returnedItemDto.getAvailable());
    }

    @Test
    void updateItem_whenOwnerIsExist_thenReturnUpdatedItem() {
        ItemDto returnedItemDto = inMemoryItemStorage.updateItem(owner.getId(), secondItemDto, itemDto.getId());

        assertEquals(1, returnedItemDto.getId());
        assertEquals("secondItemName", returnedItemDto.getName());
        assertEquals("secondItemDescription", returnedItemDto.getDescription());
        assertTrue(returnedItemDto.getAvailable());
    }

    @Test
    void updateItem_whenOwnerDoesNotMatch_thenThrowException() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> inMemoryItemStorage.updateItem(9999, secondItemDto, itemDto.getId()));

        assertEquals("Item belongs to another owner", notFoundException.getMessage());
    }

    @Test
    void getItem() {
        ItemWithBookingDto returnedItem = inMemoryItemStorage.getItem(itemDto.getId());

        assertEquals(1, returnedItem.getId());
        assertEquals("itemName", returnedItem.getName());
        assertEquals("itemDescription", returnedItem.getDescription());
        assertTrue(returnedItem.getAvailable());
        assertNull(returnedItem.getLastBooking());
        assertNull(returnedItem.getNextBooking());
        assertEquals(0, returnedItem.getComments().size());
    }

    @Test
    void getAllUserItems() {
        inMemoryItemStorage.addItem(owner, secondItemDto);

        assertEquals(2, inMemoryItemStorage.getAllUserItems(owner.getId()).size());

    }

    @Test
    void getItemsBySearch_whenTextIsEmpty_thenReturnEmptyList() {
        inMemoryItemStorage.addItem(owner, secondItemDto);

        assertEquals(0, inMemoryItemStorage.getItemsBySearch("").size());
    }

    @Test
    void getItemsBySearch_whenTextIsNotEmpty_thenReturnListWithTwoItem() {
        inMemoryItemStorage.addItem(owner, secondItemDto);

        assertEquals(2, inMemoryItemStorage.getItemsBySearch("name").size());
    }
}