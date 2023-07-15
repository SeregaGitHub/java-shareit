package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {
    ItemDto addItem(User owner, ItemDto itemDto);

    ItemDto updateItem(Integer owner, ItemDto itemDto, Integer id);

    ItemWithBookingDto getItem(Integer id);

    List<ItemWithBookingDto> getAllUserItems(Integer owner);

    List<ItemDto> getItemsBySearch(String text);

    void deleteItem(Integer id);
}
