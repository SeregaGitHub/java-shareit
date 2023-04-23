package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto addItem(Integer owner, @Valid ItemDto itemDto);

    ItemDto updateItem(Integer owner, Map<String, Object> itemFields, Integer id);

    ItemDto getItem(Integer id);

    List<ItemDto> getItems(Integer owner);

    List<ItemDto> getItemsBySearch(String text);
}
