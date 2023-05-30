package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import javax.transaction.Transactional;
import java.util.List;

public interface ItemService {
    ItemDto addItem(Integer owner, ItemDto itemDto);

    ItemDto updateItem(Integer owner, ItemDto itemDto, Integer id);

    ItemWithBookingDto getItem(Integer userId, Integer id);

    @Transactional
    List<ItemWithBookingDto> getItems(Integer owner);

    List<ItemDto> getItemsBySearch(String text);

    @Transactional
    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);
}
