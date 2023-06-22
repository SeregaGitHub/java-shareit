package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.requestUtil.RequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestHibernateServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private RequestMapper requestMapper;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestHibernateService itemRequestHibernateService;
    private User user;
    private User requester;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemWithRequestIdDto itemWithRequestIdDto;
    private ItemRequestWithItemsDto itemRequestWithItemsDto;

    @BeforeEach
    void beforeEach() {
        user = new User(0, "name", "user@yandex.ru");
        requester = new User(1, "requesterName", "requester@yandex.ru");
        LocalDateTime now = LocalDateTime.now();
        requestMapper = new RequestMapper();
        itemRequestDto = new ItemRequestDto(0, "itemRequestDtoDescription", now, requester.getId());
        itemRequest = new ItemRequest(0, "itemRequestDtoDescription", now, requester);
        itemWithRequestIdDto = new ItemWithRequestIdDto(0, "itemName", "itemDescription",
                true, itemRequestDto.getId());
        itemRequestWithItemsDto = new ItemRequestWithItemsDto(0, "itemRequestDtoDescription",
                now, requester.getId(), List.of(itemWithRequestIdDto));
    }

    @Test
    void addItemRequest_whenOwnerNotFound_whenThrowException() {
        Integer requesterId = requester.getId();
        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestHibernateService.addItemRequest(requesterId, itemRequestDto));
        assertEquals("User with Id=" + requesterId + " does not exist", notFoundException.getMessage());

        verify(userRepository, times(1)).findById(requesterId);
        verify(itemRequestRepository, never()).save(itemRequest);
    }

    @Test
    void addItemRequest_whenUserFound_thenReturnItemRequest() {
        Integer requesterId = requester.getId();
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);

        ItemRequestDto returnedItemRequest = itemRequestHibernateService.addItemRequest(requesterId, itemRequestDto);

        assertEquals(itemRequestDto, returnedItemRequest);
        verify(userRepository, times(1)).findById(requesterId);
        verify(itemRequestRepository, times(1)).save(itemRequest);
    }

    @Test
    void getItemRequestsList_whenUserDoesNotExist_whenThrowException() {
        Integer requesterId = requester.getId();
        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestHibernateService.getItemRequestsList(requesterId));
        assertEquals("User with Id=" + requesterId + " - does not exist", notFoundException.getMessage());

        verify(userRepository, times(1)).findById(requesterId);
        verify(itemRequestRepository, never()).getItemRequestsOfUserList(requesterId);
    }

    @Test
    void getItemRequestsList_whenUserWasFound_thenReturnList() {
        Integer requesterId = requester.getId();
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.getItemRequestsOfUserList(requesterId)).thenReturn(List.of(itemRequestDto));

        List<ItemRequestWithItemsDto> returnedList = itemRequestHibernateService.getItemRequestsList(requesterId);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(requesterId);
        verify(itemRequestRepository, times(1)).getItemRequestsOfUserList(requesterId);
    }

    @Test
    void getAllItemRequestsList_whenThereIsNoPageRequest_thenReturnList() {
        Integer userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.getItemRequestsList(userId))
                .thenReturn(List.of(itemRequestDto));

        List<ItemRequestWithItemsDto> returnedList = itemRequestHibernateService.getAllItemRequestsList(
                userId, null, null);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).getItemRequestsList(userId);
    }

    @Test
    void getAllItemRequestsList_whenUserDoesNotExist_whenThrowException() {
        Integer requesterId = requester.getId();
        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestHibernateService.getAllItemRequestsList(requesterId, null, null));
        assertEquals("User with Id=" + requesterId + " - does not exist", notFoundException.getMessage());

        verify(userRepository, times(1)).findById(requesterId);
        verify(itemRequestRepository, never()).getItemRequestsList(requesterId);
    }

    @Test
    void getAllItemRequestsList_whenUserWasFound_thenReturnList() {
        Integer userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.getItemRequestsList(userId, PageRequest.of(0, 1)))
                .thenReturn(List.of(itemRequestDto));

        List<ItemRequestWithItemsDto> returnedList = itemRequestHibernateService.getAllItemRequestsList(
                userId, 0, 1);

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).getItemRequestsList(
                userId, PageRequest.of(0, 1));
    }

    @Test
    void getItemRequestById_whenUserAndItemRequestAreExist_thenReturnList() {
        Integer userId = user.getId();
        Integer requestId = itemRequestDto.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.getItemsWithRequestDtoList(Set.of(requestId))).thenReturn(List.of(itemWithRequestIdDto));

        ItemRequestWithItemsDto returnedItemRequest = itemRequestHibernateService.getItemRequestById(userId, requestId);

        assertEquals(itemRequestWithItemsDto, returnedItemRequest);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(itemRepository, times(1)).getItemsWithRequestDtoList(Set.of(requestId));
    }

    @Test
    void getItemRequestById_whenUserWasNotFound_thenThrowException() {
        Integer userId = user.getId();
        Integer requestId = itemRequestDto.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestHibernateService.getItemRequestById(userId, requestId));
        assertEquals("User with Id=" + userId + " - does not exist", notFoundException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, never()).findById(requestId);
        verify(itemRepository, never()).getItemsWithRequestDtoList(Set.of(requestId));
    }

    @Test
    void getItemRequestById_whenItemRequestWasNotFound_thenThrowException() {
        Integer userId = user.getId();
        Integer requestId = itemRequestDto.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestHibernateService.getItemRequestById(userId, requestId));
        assertEquals("Item request with Id=" + requestId + " - does not exist", notFoundException.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(itemRepository, never()).getItemsWithRequestDtoList(Set.of(requestId));
    }
}