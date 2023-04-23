package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemStorage {
    ItemDto addItem(Integer owner, ItemDto itemDto);
    ItemDto updateItem(Integer owner, Map<String, Object> itemFields, Integer id);
    ItemDto getItem(Integer id);
    List<ItemDto> getAllUserItems(Integer owner);
    List<ItemDto> getItemsBySearch(String text);
}
