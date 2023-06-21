package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.itemUtil.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private InMemoryItemStorage inMemoryItemStorage;
    @Mock
    private InMemoryUserStorage inMemoryUserStorage;
    @InjectMocks
    private ItemServiceImpl itemServiceImpl;

    private User user;
    private User owner;
    private User requester;
    private ItemDto itemDto;
    private ItemWithRequestDto itemWithRequestDto;
    private ItemWithBookingDto itemWithBookingDto;
    private Item item;


    @BeforeEach
    void beforeEach() {
        user = new User(0, "name", "user@yandex.ru");
        owner = new User(1, "ownerName", "owner@yandex.ru");
        requester = new User(1, "requesterName", "requester@yandex.ru");
        itemDto = new ItemDto(0, "itemName", "itemDescription", true);
        itemWithRequestDto = new ItemWithRequestDto(0, "itemName", "itemDescription", true,
                requester.getId());
        itemWithBookingDto = new ItemWithBookingDto(0, "itemName", "itemDescription", true,
                null, null, new ArrayList<>());
        item = new Item(0, "itemName", "itemDescription", true, owner, null);
    }

    @Test
    void addItem_whenOwnerIsNotExist_whenThrowException() {
        lenient().when(inMemoryItemStorage.addItem(null, itemWithRequestDto)).thenThrow(
                new OwnerNotFoundException("Request do not contain owner of the item"));

        verify(inMemoryItemStorage, never()).addItem(null, itemWithRequestDto);
    }

    @Test
    void addItem() {
        when(inMemoryUserStorage.getUser(owner.getId())).thenReturn(owner);
        when(inMemoryItemStorage.addItem(owner, itemWithRequestDto)).thenReturn(itemWithRequestDto);

        ItemWithRequestDto returnedItemWithRequestDto = itemServiceImpl.addItem(owner.getId(), itemWithRequestDto);

        assertEquals(itemWithRequestDto, returnedItemWithRequestDto);
        verify(inMemoryItemStorage, times(1)).addItem(owner, itemWithRequestDto);
    }

    @Test
    void updateItem_whenUserNotFound_thenThrowNewNotFoundException() {
        Integer ownerId = owner.getId();
        when(inMemoryUserStorage.getUser(ownerId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> itemServiceImpl.updateItem(ownerId, itemDto, itemDto.getId()));

        verify(inMemoryUserStorage, times(1)).getUser(ownerId);
        verify(inMemoryItemStorage, never()).updateItem(ownerId, itemDto, itemDto.getId());
    }

    @Test
    void updateItem_whenItemFound_thenReturnItem() {
        Integer ownerId = owner.getId();
        ItemDto itemDtoForUpdate = new ItemDto(itemDto.getId(),
                "newItemName", "newItemDescription", true);
        Item updatedItem = ItemMapper.toItem(owner, itemDtoForUpdate);
        when(inMemoryUserStorage.getUser(ownerId)).thenReturn(owner);
        when(inMemoryItemStorage.updateItem(ownerId, itemDtoForUpdate, itemDto.getId())).thenReturn(
                ItemMapper.toItemDto(updatedItem));

        itemServiceImpl.updateItem(ownerId, itemDtoForUpdate, itemDto.getId());
        assertEquals(ItemMapper.toItem(owner, itemDtoForUpdate),
                ReflectionTestUtils.invokeMethod(InMemoryItemStorage.class, "makeItem", item, itemDtoForUpdate));
    }

    @Test
    void getItem() {
        when(inMemoryItemStorage.getItem(item.getId())).thenReturn(itemWithBookingDto);

        ItemWithBookingDto returnedItemWithBookingDto = itemServiceImpl.getItem(owner.getId(), item.getId());

        assertEquals(itemWithBookingDto, returnedItemWithBookingDto);
    }

    @Test
    void getItems() {
        when(inMemoryItemStorage.getAllUserItems(owner.getId())).thenReturn(List.of(itemWithBookingDto));

        List<ItemWithBookingDto> returnedList = itemServiceImpl.getItems(owner.getId(), 0, 1);

        assertEquals(1, returnedList.size());
    }

    @Test
    void getItemsBySearch_whenTextIsEmpty_thenReturnEmptyList() {
        when(inMemoryItemStorage.getItemsBySearch("")).thenReturn(new ArrayList<>());

        List<ItemDto> returnedList = itemServiceImpl.getItemsBySearch("", 0, 1);

        assertEquals(0, returnedList.size());
    }

    @Test
    void getItemsBySearch_whenTextIsNotEmpty_thenReturnListWithOneItem() {
        when(inMemoryItemStorage.getItemsBySearch("item")).thenReturn(List.of(itemDto));

        List<ItemDto> returnedList = itemServiceImpl.getItemsBySearch("item", 0, 1);

        assertEquals(1, returnedList.size());
    }
}