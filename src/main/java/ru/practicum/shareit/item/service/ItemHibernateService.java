package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.bookingUtil.Status;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.CommentErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShot;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.itemUtil.ItemMapper;
import ru.practicum.shareit.item.itemUtil.ItemUtil;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class ItemHibernateService implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(Integer owner, ItemDto itemDto) {
        Integer itemId = itemRepository.save(ItemMapper.toItem(userRepository.findById(owner).orElseThrow(
                () -> new NotFoundException("User with Id=" + owner + " - does not exist")), itemDto)).getId();
        itemDto.setId(itemId);
        log.info("Item with name={} was added", itemDto.getName());
        return itemDto;
    }

    @Override
    public ItemDto updateItem(Integer owner, ItemDto itemDto, Integer id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "Item with Id=" + id + " - does not exist"));
        if (Objects.equals(owner, item.getOwner().getId())) {
            log.info("Item with Id={} was updated", id);
            itemRepository.save(item = ItemUtil.makeItem(item, itemDto));
        } else {
            throw new NotFoundException("Item belongs to another owner");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemWithBookingDto getItem(Integer userId, Integer id) {
        Item item = itemRepository.findItemByIdWithOwner(id).orElseThrow(
                () -> new NotFoundException("Item with Id=" + id + " - does not exist"));
        List<CommentDto> commentDtoList = commentRepository.getItemComment(id);

        if (Objects.equals(userId, item.getOwner().getId())) {
            LocalDateTime now = LocalDateTime.now();
            List<BookingForItemDto> bookingList = bookingRepository.getLastAndNextBooking(id, now);
            return ItemMapper.toItemWithBookingAndCommentDto(item, bookingList, now, commentDtoList);
        } else {
            return ItemMapper.toItemNoBookingDto(item, commentDtoList);
        }
    }

    @Override
    @Transactional
    public List<ItemWithBookingDto> getItems(Integer owner) {
        List<ItemShot> itemShots = itemRepository.findItemsShotByOwner_Id(owner);
        List<ItemWithBookingDto> dtoList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (ItemShot i: itemShots) {
        //   Я не смог написать запрос, который выдавал бы все last и nextBooking для нескольких Item
        // чтобы затем разобрать в методе. (смог только для одного - getLastAndNextBooking()).
        // Поэтому пришлось получать бронирования в цикле...
            List<BookingForItemDto> bookingList = bookingRepository.getLastAndNextBooking(i.getId(), now);
            dtoList.add(ItemMapper.toItemWithBookingDto(i, bookingList, now));
        }
        return dtoList;
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.findByAvailableTrueAndNameIgnoreCaseOrAvailableTrueAndDescriptionIgnoreCaseContaining(text, text)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        List<BookingForItemDto> list = bookingRepository.getBookingDtoByBooker_IdAndItem_Id(
                userId, itemId, Status.APPROVED, PageRequest.of(0, 1));

        BookingForItemDto bookingDto;
        if (!list.isEmpty()) {
            bookingDto = list.stream().findFirst().get();
        } else {
            throw new CommentErrorException("User with Id=" + userId + " did not book item with Id=" + itemId);
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(bookingDto.getEndTime())) {
            throw new CommentErrorException("You can not create comment before your booking is not end");
        }
// Метод get() без проверки isPresent() т.к. существование user и item проверены методом - getBookingDtoByBooker_IdAndItem_Id()
        User user = userRepository.findById(userId).get();
        Integer commentId = commentRepository.save(CommentMapper.toComment(user,
                itemRepository.findItemByIdWithOwner(itemId).get(), commentDto, now)).getId();
        log.info("Comment to Item with Id={} was added by User with Id={}", itemId, userId);
        return CommentDto.builder()
                .id(commentId)
                .text(commentDto.getText())
                .authorName(user.getName())
                .created(now)
                .build();
    }
}
