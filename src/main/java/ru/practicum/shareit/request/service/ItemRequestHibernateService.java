package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.util.RequestPaginationValid;
import ru.practicum.shareit.request.requestUtil.RequestCollectionMapper;
import ru.practicum.shareit.request.requestUtil.RequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestHibernateService implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addItemRequest(Integer userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with Id=" + userId + " does not exist"));

        itemRequestDto.setRequester(userId);
        itemRequestDto.setCreated(LocalDateTime.now());

        ItemRequest itemRequest = requestMapper.toItemRequest(itemRequestDto, user);
        Integer requestId = itemRequestRepository.save(itemRequest).getId();

        itemRequestDto.setId(requestId);
        log.info("Request with Id=" + requestId + " was added");
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestWithItemsDto> getItemRequestsList(Integer userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with Id=" + userId + " - does not exist"));

        List<ItemRequestDto> itemRequestDtoList = itemRequestRepository.getItemRequestsOfUserList(userId);

        return RequestCollectionMapper.makeItemRequestWithItemsDtoList(itemRequestDtoList, itemRepository, requestMapper);
    }

    @Override
    public List<ItemRequestWithItemsDto> getAllItemRequestsList(Integer userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with Id=" + userId + " - does not exist"));
        RequestPaginationValid.requestPaginationValid(from, size);

        List<ItemRequestDto> itemRequestList;
        if (from == null || size == null) {
            itemRequestList = itemRequestRepository.getItemRequestsList(userId);
        } else {
            itemRequestList = itemRequestRepository.getItemRequestsList(userId, PageRequest.of(from > 0 ? from / size : 0, size));
        }
        return RequestCollectionMapper.makeItemRequestWithItemsDtoList(itemRequestList, itemRepository, requestMapper);
    }

    @Override
    public ItemRequestWithItemsDto getItemRequestById(Integer userId, Integer requestId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with Id=" + userId + " - does not exist"));
        ItemRequestDto itemRequestDto = requestMapper.toItemRequestDto(itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Item request with Id=" + requestId + " - does not exist")));

        List<ItemWithRequestIdDto> list = itemRepository.getItemsWithRequestDtoList(Set.of(requestId));
        return requestMapper.toItemRequestWithItemsDto(itemRequestDto, list);
    }
}
