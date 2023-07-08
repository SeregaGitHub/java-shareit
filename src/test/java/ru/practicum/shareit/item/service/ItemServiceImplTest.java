package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.bookingUtil.Status;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.CommentErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.itemUtil.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemServiceImpl;
    private User user;
    private User requester;
    private Item item;
    private ItemDto itemDto;
    private ItemWithRequestDto itemWithRequestDto;
    private ItemWithBookingDto itemWithBookingDto;
    private CommentDto commentDto;
    private LocalDateTime now;
    private Comment comment;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        user = new User(0, "name", "user@yandex.ru");
        requester = new User(1, "requesterName", "requester@yandex.ru");
        itemDto = new ItemDto(0, "itemName", "itemDescription", true);
        commentDto = new CommentDto(0, "text", "name", now);
        ItemRequest itemRequest = new ItemRequest(0, "itemN", now, requester);
        itemWithRequestDto = new ItemWithRequestDto(0, "itemName", "itemDescription", true,
                requester.getId());
        itemWithBookingDto = new ItemWithBookingDto(0, "itemName", "itemDescription", true,
                null, null, List.of(commentDto));
        item = ItemMapper.toItem(user, itemWithRequestDto, itemRequest);
        comment = Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .commentUser(user)
                .itemForComment(item)
                .created(commentDto.getCreated())
                .build();
    }

    @Test
    void addItem_whenOwnerNotFound_whenThrowException() {
        Integer userId = item.getOwner().getId();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemServiceImpl.addItem(userId, itemWithRequestDto));

        assertEquals("User with Id=" + userId + " - does not exist", notFoundException.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, never()).save(item);
    }

    @Test
    void addItem_whenUserFound_thenReturnItem() {
        Integer userId = user.getId();
        Integer requestId = itemWithRequestDto.getRequestId();
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);

        User returnedUser = userRepository.findById(userId).get();
        Item returnedItem = itemRepository.save(item);
        ItemRequest returnedItemRequest = itemRequestRepository.findById(requestId).orElse(null);

        assertNull(returnedItemRequest);
        assertEquals(user, returnedUser);
        assertEquals(item, returnedItem);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItem_whenItemNotFound_whenThrowException() {
        Integer itemId = itemDto.getId();
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemServiceImpl.updateItem(user.getId(), itemDto, itemDto.getId()));

        assertEquals("Item with Id=" + itemId + " - does not exist", notFoundException.getMessage());
        verify(itemRepository, times(1)).findById(itemDto.getId());
        verify(itemRepository, never()).save(item);
    }

    @Test
    void updateItem_whenOwnerNotFound_whenThrowException() {
        Integer itemId = itemDto.getId();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemServiceImpl.updateItem(9999, itemDto, itemDto.getId()));

        assertEquals("Item belongs to another owner", notFoundException.getMessage());
        verify(itemRepository, times(1)).findById(itemDto.getId());
        verify(itemRepository, never()).save(item);
    }

    @Test
    void updateItem_whenUserIsOwner_thenReturnItem() {
        ItemDto updatedItem = new ItemDto(0, "newName", "newDescription", true);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        itemServiceImpl.updateItem(user.getId(), updatedItem, itemDto.getId());

        verify(itemRepository, times(1)).save(itemArgumentCaptor.capture());
        Item captorItem = itemArgumentCaptor.getValue();
        assertEquals(0, captorItem.getId());
        assertEquals("newName", captorItem.getName());
        assertEquals("newDescription", captorItem.getDescription());
    }

    @Test
    void getItem_whenItemNotFound_thenThrowNewNotFoundException() {
        Integer itemId = item.getId();
        when(itemRepository.findItemByIdWithOwner(itemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemServiceImpl.getItem(user.getId(), itemId));

        assertEquals("Item with Id=" + itemId + " - does not exist", exception.getMessage());
        verify(itemRepository, times(1)).findItemByIdWithOwner(itemId);
        verify(commentRepository, never()).getItemComment(itemId);
        verify(bookingRepository, never()).getLastAndNextBooking(itemId, LocalDateTime.now());
    }

    @Test
    void getItem_whenItemFound_thenReturnItemWithBooking() {
        Integer itemId = item.getId();
        when(itemRepository.findItemByIdWithOwner(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.getItemComment(itemId)).thenReturn(List.of(commentDto));

        ItemWithBookingDto returnedItem = itemServiceImpl.getItem(user.getId(), itemId);

        assertEquals(itemWithBookingDto, returnedItem);
        verify(itemRepository, times(1)).findItemByIdWithOwner(itemId);
        verify(commentRepository, times(1)).getItemComment(itemId);
    }

    @Test
    void getItem_whenItemFound_thenReturnItemWithNoBooking() {
        Integer itemId = item.getId();
        when(itemRepository.findItemByIdWithOwner(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.getItemComment(itemId)).thenReturn(List.of(commentDto));

        ItemWithBookingDto returnedItem = itemServiceImpl.getItem(9999, itemId);

        assertEquals(itemWithBookingDto, returnedItem);
        verify(itemRepository, times(1)).findItemByIdWithOwner(itemId);
        verify(commentRepository, times(1)).getItemComment(itemId);
        verify(bookingRepository, never()).getLastAndNextBooking(itemId, now);
    }

    @Test
    void getItems_whenPageRequestIs01_thenReturnList() {
        Integer userId = user.getId();
        when(itemRepository.findItemsShotByOwner_Id(userId, PageRequest.of(0, 1)))
                .thenReturn(List.of(new ItemShot() {
                    @Override
                    public Integer getId() {
                        return 0;
                    }

                    @Override
                    public String getName() {
                        return "itemName";
                    }

                    @Override
                    public String getDescription() {
                        return "itemDescription";
                    }

                    @Override
                    public Boolean getAvailable() {
                        return true;
                    }
                }));

        List<ItemWithBookingDto> returnedList = itemServiceImpl.getItems(userId, 0, 1);

        assertEquals(1, returnedList.size());
        verify(itemRepository, times(1)).findItemsShotByOwner_Id(userId, PageRequest.of(0, 1));
    }

    @Test
    void getItems_whenThereIsNoPageRequest_thenReturnList() {
        Integer userId = user.getId();
        when(itemRepository.findItemsShotByOwner_Id(userId))
                .thenReturn(List.of(new ItemShot() {
                    @Override
                    public Integer getId() {
                        return 0;
                    }

                    @Override
                    public String getName() {
                        return "itemName";
                    }

                    @Override
                    public String getDescription() {
                        return "itemDescription";
                    }

                    @Override
                    public Boolean getAvailable() {
                        return true;
                    }
                }));

        List<ItemWithBookingDto> returnedList = itemServiceImpl.getItems(userId, null, null);

        assertEquals(1, returnedList.size());
        verify(itemRepository, times(1)).findItemsShotByOwner_Id(userId);
    }

    @Test
    void getItemsBySearch_whenPageRequestIs01_thenReturnList() {
        when(itemRepository.findByAvailableTrueAndNameIgnoreCaseOrAvailableTrueAndDescriptionIgnoreCaseContaining(
                "itemN", "itemN", PageRequest.of(0, 1)
        )).thenReturn(List.of(item));

        List<ItemDto> returnedList = itemServiceImpl.getItemsBySearch("itemN", 0, 1);

        assertEquals(1, returnedList.size());
        verify(itemRepository, times(1)).findByAvailableTrueAndNameIgnoreCaseOrAvailableTrueAndDescriptionIgnoreCaseContaining(
                "itemN", "itemN", PageRequest.of(0, 1)
        );
    }

    @Test
    void getItemsBySearch_whenThereIsNoPageRequest_thenReturnList() {
        when(itemRepository.findByAvailableTrueAndNameIgnoreCaseOrAvailableTrueAndDescriptionIgnoreCaseContaining(
                "itemN", "itemN")).thenReturn(List.of(item));

        List<ItemDto> returnedList = itemServiceImpl.getItemsBySearch("itemN", null, null);

        assertEquals(1, returnedList.size());
        verify(itemRepository, times(1)).findByAvailableTrueAndNameIgnoreCaseOrAvailableTrueAndDescriptionIgnoreCaseContaining(
                "itemN", "itemN");
    }

    @Test
    void getItemsBySearch_whenTextIsBlank_thenReturnList() {
        lenient().when(itemRepository.findByAvailableTrueAndNameIgnoreCaseOrAvailableTrueAndDescriptionIgnoreCaseContaining(
                "", "")).thenReturn(new ArrayList<>());

        List<ItemDto> returnedList = itemServiceImpl.getItemsBySearch("", null, null);

        assertEquals(0, returnedList.size());
        verify(itemRepository, never()).findByAvailableTrueAndNameIgnoreCaseOrAvailableTrueAndDescriptionIgnoreCaseContaining(
                "", "");
    }

    @Test
    void addComment_whenBookingIsExist_thenReturnComment() {
        Integer userId = user.getId();
        Integer itemId = item.getId();
        when(bookingRepository.getBookingDtoByBooker_IdAndItem_Id(userId, itemId, Status.APPROVED,
                PageRequest.of(0, 1))).thenReturn(List.of(
                        new BookingForItemDto(0, now.minusHours(2), now.minusHours(1), itemId, requester.getId())));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findItemByIdWithOwner(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.save(comment)).thenReturn(comment);


        CommentDto returnedComment = itemServiceImpl.addComment(userId, itemId, commentDto);

        assertEquals(commentDto, returnedComment);
        verify(bookingRepository, times(1)).getBookingDtoByBooker_IdAndItem_Id(userId, itemId,
                Status.APPROVED, PageRequest.of(0, 1));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findItemByIdWithOwner(itemId);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void addComment_whenBookingIsNotExist_thenThrowException() {
        Integer userId = user.getId();
        Integer itemId = item.getId();
        when(bookingRepository.getBookingDtoByBooker_IdAndItem_Id(userId, itemId, Status.APPROVED,
                PageRequest.of(0, 1))).thenReturn(List.of(
                new BookingForItemDto(0, now.minusHours(2), now.plusHours(1), itemId, requester.getId())));

        CommentErrorException exception = assertThrows(CommentErrorException.class,
                () -> itemServiceImpl.addComment(userId, itemId, commentDto));
        assertEquals("You can not create comment before your booking is not end", exception.getMessage());

        verify(bookingRepository, times(1)).getBookingDtoByBooker_IdAndItem_Id(userId, itemId,
                Status.APPROVED, PageRequest.of(0, 1));
        verify(userRepository, never()).findById(userId);
        verify(itemRepository, never()).findItemByIdWithOwner(itemId);
        verify(commentRepository, never()).save(comment);
    }

    @Test
    void addComment_whenBookingTimeIsNotOver_thenThrowException() {
        Integer userId = user.getId();
        Integer itemId = item.getId();
        when(bookingRepository.getBookingDtoByBooker_IdAndItem_Id(userId, itemId, Status.APPROVED,
                PageRequest.of(0, 1))).thenReturn(new ArrayList<>());

        CommentErrorException exception = assertThrows(CommentErrorException.class,
                () -> itemServiceImpl.addComment(userId, itemId, commentDto));
        assertEquals("User with Id=" + userId + " did not book item with Id=" + itemId, exception.getMessage());

        verify(bookingRepository, times(1)).getBookingDtoByBooker_IdAndItem_Id(userId, itemId,
                Status.APPROVED, PageRequest.of(0, 1));
        verify(userRepository, never()).findById(userId);
        verify(itemRepository, never()).findItemByIdWithOwner(itemId);
        verify(commentRepository, never()).save(comment);
    }
}