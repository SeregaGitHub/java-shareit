package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private Integer itemId = 0;
    private final Map<Integer, Item> items = new HashMap<>();
    private final Map<Integer, List<Integer>> userItems = new HashMap<>();

    @Override
    public ItemDto addItem(Integer owner, ItemDto itemDto) {
        itemDto.setId(++itemId);
        Item item = ItemMapper.toItem(owner, itemDto);
        items.put(item.getId(), item);
        Optional.ofNullable(userItems.get(owner)).ifPresentOrElse(v -> v.add(item.getId()),
                                                 () -> userItems.put(owner, new ArrayList<>(List.of(item.getId()))));
        return itemDto;
    }

    @Override
    public ItemDto updateItem(Integer owner, Map<String, Object> itemFields, Integer id) {
        Item item = items.get(id);
        if (Objects.equals(item.getOwner(), owner)) {
            itemFields.forEach((k, v) -> {
                Field field = ReflectionUtils.findField(Item.class, k);
                assert field != null;
                field.setAccessible(true);
                ReflectionUtils.setField(field, item, v);
            });
        } else {
            throw new UserNotFoundException("Item belongs to another owner");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(Integer id) {
        return ItemMapper.toItemDto(items.get(id));
    }

    @Override
    public List<ItemDto> getAllUserItems(Integer owner) {
        return userItems.get(owner)
                .stream()
                .map(v -> ItemMapper.toItemDto(items.get(v)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        return text.isEmpty() ? new ArrayList<>() : items.values().stream()
                                                    .filter(v -> v.getName().toLowerCase().contains(text) ||
                                                            v.getDescription().toLowerCase().contains(text))
                                                    .filter(Item::getAvailable)
                                                    .map(ItemMapper::toItemDto)
                                                    .collect(Collectors.toList());
    }
}
