package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.mark.Create;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
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
}
