package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Integer owner, ItemDto itemDto);

    ItemDto updateItem(Integer owner, ItemDto itemDto, Integer id);

    ItemDto getItem(Integer id);

    List<ItemDto> getItems(Integer owner);

    List<ItemDto> getItemsBySearch(String text);
}
