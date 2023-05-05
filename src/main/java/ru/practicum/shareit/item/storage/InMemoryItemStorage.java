package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private Integer itemId = 0;
    private final Map<Integer, Item> items = new HashMap<>();
    private final Map<Integer, List<Integer>> userItems = new HashMap<>();

    @Override
    public ItemDto addItem(User owner, ItemDto itemDto) {
        itemDto.setId(++itemId);
        Item item = ItemMapper.toItem(owner, itemDto);
        items.put(item.getId(), item);
        Optional.ofNullable(userItems.get(owner.getId())).ifPresentOrElse(v -> v.add(item.getId()),
                () -> userItems.put(owner.getId(), new ArrayList<>(List.of(item.getId()))));
        return itemDto;
    }

    @Override
    public ItemDto updateItem(Integer owner, ItemDto itemDto, Integer id) {
        Item item = items.get(id);
        if (Objects.equals(item.getOwner().getId(), owner)) {
            item = makeItem(item, itemDto);
            items.put(item.getId(), item);
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
                                                         .filter(Item::getAvailable)
                                                         .filter(v -> v.getName().toLowerCase().contains(text) ||
                                                                 v.getDescription().toLowerCase().contains(text))
                                                         .map(ItemMapper::toItemDto)
                                                         .collect(Collectors.toList());
    }

    private Item makeItem(Item item, ItemDto itemDto) {
        return Item.builder()
                .id(item.getId())
                .name(itemDto.getName() == null ? item.getName() : itemDto.getName())
                .description(itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription())
                .available(itemDto.getAvailable() == null ? item.getAvailable() : itemDto.getAvailable())
                .owner(item.getOwner())
                .build();
    }
}
