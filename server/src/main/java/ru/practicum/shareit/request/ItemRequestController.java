package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        itemRequestService.addItemRequest(userId, itemRequestDto);
        return itemRequestDto;
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getItemRequestsList(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getItemRequestsList(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                      @PathVariable("requestId") Integer requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemsDto> getAllItemRequestsList(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                                @RequestParam(value = "from", required = false) Integer from,
                                                                @RequestParam(value = "size", required = false) Integer size) {
        return itemRequestService.getAllItemRequestsList(userId, from, size);
    }
}
