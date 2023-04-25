package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface ItemStorage {
    ItemDto addItem(User owner, ItemDto itemDto);

    ItemDto updateItem(Integer owner, ItemDto itemDto, Integer id);

    ItemDto getItem(Integer id);

    List<ItemDto> getAllUserItems(Integer owner);

    List<ItemDto> getItemsBySearch(String text);
}
