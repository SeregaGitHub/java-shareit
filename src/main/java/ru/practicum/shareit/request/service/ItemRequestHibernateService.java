package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.requestUtil.RequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestHibernateService implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;

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
}
