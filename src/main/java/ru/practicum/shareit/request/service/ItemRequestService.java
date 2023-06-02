package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(Integer userId, ItemRequestDto itemRequestDto);
}
