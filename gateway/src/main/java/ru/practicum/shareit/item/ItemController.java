package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.util.mark.Create;
import ru.practicum.shareit.util.mark.Update;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Integer owner,
                                          @Validated(Create.class) @RequestBody ItemWithRequestDto itemWithRequestDto) {
        return itemClient.addItem(owner, itemWithRequestDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Integer owner,
                                             @RequestBody @Validated(Update.class) ItemDto itemDto, @PathVariable("id") Integer id) {
        return itemClient.updateItem(owner, itemDto, id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("id") Integer id) {
        return itemClient.getItem(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Integer owner,
                                             @RequestParam(value = "from", defaultValue = "0") Integer from,
                                             @RequestParam(value = "size", defaultValue = "2147483647") Integer size) {
        return itemClient.getItems(owner, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearch(@RequestParam String text,
                                          @RequestParam(value = "from", defaultValue = "0") Integer from,
                                          @RequestParam(value = "size", defaultValue = "2147483647") Integer size) {
        return itemClient.getItemsBySearch(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("itemId") Integer itemId,
                                 @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
