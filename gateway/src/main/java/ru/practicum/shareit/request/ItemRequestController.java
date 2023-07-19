package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.mark.Create;

@RestController
@RequestMapping(path = "/requests")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("User with Id={} creating item request {}", userId, itemRequestDto);
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsList(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Getting item requests by user with Id={}", userId);
        return itemRequestClient.getItemRequestsList(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                     @PathVariable("requestId") Integer requestId) {
        log.info("Getting by user with Id={} item request with Id={}", userId, requestId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestsList(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                         @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                         @RequestParam(value = "size",
                                                                 defaultValue = "2147483647") Integer size) {
        log.info("Getting all item requests by user with Id={}", userId);
        return itemRequestClient.getAllItemRequestsList(userId, from, size);
    }
}
