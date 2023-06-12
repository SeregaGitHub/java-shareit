package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;
    private User user;
    private LocalDateTime ldt;
    //private ItemRequest itemRequest;
    private ItemRequestWithItemsDto itemRequestWithItemsDto;
    private ItemRequestDto itemRequestDto;
    private ItemWithRequestIdDto itemWithRequestIdDto;


    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate localDate = now.toLocalDate();
        LocalTime localTime = LocalTime.parse(now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        ldt = LocalDateTime.of(localDate, localTime);

        user = new User(0, "name", "email@yandex.ru");
        itemRequestDto = new ItemRequestDto(0, "requestDescription", ldt, user.getId());
        //itemRequest = new ItemRequest(0, "requestDescription", ldt, user);
        itemWithRequestIdDto = new ItemWithRequestIdDto(0, "itemName", "itemDescription", true, 1);
        itemRequestWithItemsDto = new ItemRequestWithItemsDto(0, "requestDescription", ldt, user.getId(),
                List.of(itemWithRequestIdDto));
    }

    @Test
    void addRequest() {
        Integer userId = user.getId();
        when(itemRequestService.addItemRequest(userId, itemRequestDto)).thenReturn(itemRequestDto);

        ItemRequestDto returnedItemRequestDto = itemRequestController.addRequest(userId, itemRequestDto);

        assertEquals(itemRequestDto, returnedItemRequestDto);
        verify(itemRequestService, times(1)).addItemRequest(userId, itemRequestDto);
    }

    @Test
    void getItemRequestsList() {
        Integer userId = user.getId();
        when(itemRequestService.getItemRequestsList(userId)).thenReturn(List.of(itemRequestWithItemsDto));

        List<ItemRequestWithItemsDto> returnedList = itemRequestController.getItemRequestsList(userId);

        assertEquals(1, returnedList.size());
        verify(itemRequestService, times(1)).getItemRequestsList(userId);
    }

    @Test
    void getItemRequestById() {
        Integer userId = user.getId();
        Integer requestId = itemRequestWithItemsDto.getId();
        when(itemRequestService.getItemRequestById(userId, requestId)).thenReturn(itemRequestWithItemsDto);

        ItemRequestWithItemsDto returnedItemRequest = itemRequestController.getItemRequestById(userId, requestId);

        assertEquals(itemRequestWithItemsDto, returnedItemRequest);
        verify(itemRequestService, times(1)).getItemRequestById(userId, requestId);
    }

    @Test
    void getAllItemRequestsList() {
        Integer userId = user.getId();
        when(itemRequestService.getAllItemRequestsList(userId, 0, 1)).thenReturn(List.of(itemRequestWithItemsDto));

        List<ItemRequestWithItemsDto> returnedList = itemRequestController.getAllItemRequestsList(userId, 0, 1);

        assertEquals(1, returnedList.size());
        verify(itemRequestService, times(1)).getAllItemRequestsList(userId, 0, 1);
    }
}