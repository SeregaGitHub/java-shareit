package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemRequestWithItemsDto> getItemRequestsList(Integer userId);

    List<ItemRequestWithItemsDto> getAllItemRequestsList(Integer userId, Integer from, Integer size);

    ItemRequestWithItemsDto getItemRequestById(Integer userId, Integer requestId);
}
