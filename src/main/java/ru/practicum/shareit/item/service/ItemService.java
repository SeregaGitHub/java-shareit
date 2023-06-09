package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;

import javax.transaction.Transactional;
import java.util.List;

public interface ItemService {
    ItemWithRequestDto addItem(Integer owner, ItemWithRequestDto itemWithRequestDto);

    ItemDto updateItem(Integer owner, ItemDto itemDto, Integer id);

    ItemWithBookingDto getItem(Integer userId, Integer id);

    @Transactional
    List<ItemWithBookingDto> getItems(Integer owner, Integer from, Integer size);

    List<ItemDto> getItemsBySearch(String text, Integer from, Integer size);

    @Transactional
    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);
}
