package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.util.Utilities;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(Integer owner, @Valid ItemDto itemDto) {
        Utilities.checkUserExist(owner, userStorage);
        log.info("Item with name={} was added", itemDto.getName());
        itemStorage.addItem(owner, itemDto);
        return itemDto;
    }

    @Override
    public ItemDto updateItem(Integer owner, Map<String, Object> itemFields, Integer id) {
        Utilities.checkUserExist(owner, userStorage);
        return itemStorage.updateItem(owner, itemFields, id);
    }

    @Override
    public ItemDto getItem(Integer id) {
        log.info("Item with Id={} was viewed", id);
        return itemStorage.getItem(id);
    }

    @Override
    public List<ItemDto> getItems(Integer owner) {
        log.info("All items of user with Id={} was viewed", owner);
        return itemStorage.getAllUserItems(owner);
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        log.info("All items with name or description like: {} - was viewed", text);
        return itemStorage.getItemsBySearch(text.toLowerCase());
    }
}
