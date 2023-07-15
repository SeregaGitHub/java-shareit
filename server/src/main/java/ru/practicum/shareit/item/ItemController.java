package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.util.mark.Create;
import ru.practicum.shareit.util.mark.Update;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemWithRequestDto addItem(@RequestHeader("X-Sharer-User-Id") Integer owner,
                           @Validated(Create.class) @RequestBody ItemWithRequestDto itemWithRequestDto) {
        itemService.addItem(owner, itemWithRequestDto);
        return itemWithRequestDto;
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer owner,
                              @RequestBody @Validated(Update.class) ItemDto itemDto, @PathVariable("id") Integer id) {
        return itemService.updateItem(owner, itemDto, id);
    }

    @GetMapping("/{id}")
    public ItemWithBookingDto getItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("id") Integer id) {
        return itemService.getItem(userId, id);
    }

    @GetMapping
    public List<ItemWithBookingDto> getItems(@RequestHeader("X-Sharer-User-Id") Integer owner,
                                             @RequestParam(value = "from", required = false) Integer from,
                                             @RequestParam(value = "size", required = false) Integer size) {
        return itemService.getItems(owner, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearch(@RequestParam String text,
                                          @RequestParam(value = "from", required = false) Integer from,
                                          @RequestParam(value = "size", required = false) Integer size) {
        return itemService.getItemsBySearch(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("itemId") Integer itemId,
                                 @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
