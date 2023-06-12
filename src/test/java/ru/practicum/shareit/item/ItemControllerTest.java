package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private User user;
    private ItemDto itemDto;
    private ItemWithRequestDto itemWithRequestDto;
    private ItemWithBookingDto itemWithBookingDto;
    private CommentDto commentDto;


    @BeforeEach
    void beforeEach() {
        user = new User(0, "name", "user@yandex.ru");
        itemDto = new ItemDto(0, "itemName", "itemDescription", true);
        itemWithRequestDto = new ItemWithRequestDto(0, "itemName", "itemDescription", true, 1);
        commentDto = new CommentDto(0, "text", "author", LocalDateTime.now());
        itemWithBookingDto = new ItemWithBookingDto(0, "itemName", "itemDescription", true,
                null, null, List.of(commentDto));
    }

    @Test
    void addItem() {
        Integer ownerId = user.getId();
        when(itemService.addItem(ownerId, itemWithRequestDto)).thenReturn(itemWithRequestDto);

        ItemWithRequestDto returnedItem = itemController.addItem(ownerId, itemWithRequestDto);

        assertEquals(itemWithRequestDto, returnedItem);
        verify(itemService, times(1)).addItem(ownerId, itemWithRequestDto);
    }

    @Test
    void updateItem() {
        Integer ownerId = user.getId();
        Integer itemId = itemDto.getId();
        when(itemService.updateItem(ownerId, itemDto, itemId)).thenReturn(itemDto);

        ItemDto returnedItemDto = itemController.updateItem(ownerId, itemDto, itemId);

        assertEquals(itemDto, returnedItemDto);
        verify(itemService, times(1)).updateItem(ownerId, itemDto, itemId);
    }

    @Test
    void getItem() {
        Integer userId = user.getId();
        Integer itemId = itemWithBookingDto.getId();
        when(itemService.getItem(userId, itemId)).thenReturn(itemWithBookingDto);

        ItemWithBookingDto returnedItemDto = itemController.getItem(userId, itemId);

        assertEquals(itemWithBookingDto, returnedItemDto);
        verify(itemService, times(1)).getItem(userId, itemId);
    }

    @Test
    void getItems() {
        Integer userId = user.getId();
        when(itemService.getItems(userId, 0, 1)).thenReturn(List.of(itemWithBookingDto));

        List<ItemWithBookingDto> returnedItemDtoList = itemController.getItems(userId, 0, 1);

        assertEquals(1, returnedItemDtoList.size());
        verify(itemService, times(1)).getItems(userId, 0, 1);
    }

    @Test
    void getItemsBySearch() {
        when(itemService.getItemsBySearch("itemN", 0, 1)).thenReturn(List.of(itemDto));

        List<ItemDto> returnedItemDtoList = itemController.getItemsBySearch("itemN", 0, 1);

        assertEquals(1, returnedItemDtoList.size());
        verify(itemService, times(1)).getItemsBySearch("itemN", 0, 1);
    }

    @Test
    void addComment() {
        Integer userId = user.getId();
        Integer itemDtoId = itemDto.getId();
        when(itemService.addComment(userId, itemDtoId, commentDto)).thenReturn(commentDto);

        CommentDto returnedCommentDto = itemController.addComment(userId, itemDtoId, commentDto);

        assertEquals(commentDto, returnedCommentDto);
        verify(itemService, times(1)).addComment(userId, itemDtoId, commentDto);
    }
}