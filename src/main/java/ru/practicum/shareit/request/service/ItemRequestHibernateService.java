package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.requestUtil.RequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
        List<ItemRequestWithItemsDto> result = new ArrayList<>();
        List<ItemRequestDto> itemRequestDtoList = itemRequestRepository.getItemRequestsList(userId);

        if (!itemRequestDtoList.isEmpty()) {
            Set<Integer> requestsId = itemRequestDtoList.stream().map(ItemRequestDto::getRequester).collect(Collectors.toSet());
            List<ItemWithRequestIdDto> itemWithRequestIdDtoList = itemRepository.getItemsWithRequestDtoList(requestsId);

            for (ItemRequestDto i: itemRequestDtoList) {
                List<ItemWithRequestIdDto> list = new ArrayList<>();
                for (int j = 0; j < itemWithRequestIdDtoList.size(); j++) {
                    if (Objects.equals(i.getId(), itemWithRequestIdDtoList.get(j).getRequestId())) {
                        list.add(itemWithRequestIdDtoList.get(j));
                        itemWithRequestIdDtoList.remove(j);
                        j--;
 // Обе коллекции отсортированы по RequestId desc. Вложенный цикл прекращает работу как только RequestId перестают совпадать
                 // и каждый раз при совпадении элемент из коллекции вложенного цикла - удаляется.
                               // Всё для того чтобы не было повторных проходов.
                    } else {
                        break;
                    }
                }
                result.add(requestMapper.toItemRequestWithItemsDto(i, list));
            }
        }
        return result;
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
