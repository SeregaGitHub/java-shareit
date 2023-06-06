package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.util.Utilities;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemWithRequestDto addItem(Integer owner, ItemWithRequestDto itemDto) {
        Utilities.checkUserExist(owner, userStorage);
        log.info("Item with name={} was added", itemDto.getName());
        itemStorage.addItem(userStorage.getUser(owner), itemDto);
        return itemDto;
    }

    @Override
    public ItemDto updateItem(Integer owner, ItemDto itemDto, Integer id) {
        Utilities.checkUserExist(owner, userStorage);
        return itemStorage.updateItem(owner, itemDto, id);
    }

    @Override
    public ItemWithBookingDto getItem(Integer userId, Integer id) {
        log.info("Item with Id={} was viewed", id);
        return itemStorage.getItem(id);
    }

    @Override
    public List<ItemWithBookingDto> getItems(Integer owner, Integer from, Integer size) {
        log.info("All items of user with Id={} was viewed", owner);
        return itemStorage.getAllUserItems(owner);
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text, Integer from, Integer size) {
        log.info("All items with name or description like: {} - was viewed", text);
        return itemStorage.getItemsBySearch(text.toLowerCase());
    }

    @Override
    public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        return null;
    }
}
