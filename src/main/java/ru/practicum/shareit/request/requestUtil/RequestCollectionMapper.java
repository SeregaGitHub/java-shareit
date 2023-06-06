package ru.practicum.shareit.request.requestUtil;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class RequestCollectionMapper {
    public List<ItemRequestWithItemsDto> makeItemRequestWithItemsDtoList(List<ItemRequestDto> itemRequestDtoList,
                                                                         ItemRepository itemRepository,
                                                                         RequestMapper requestMapper) {
        List<ItemRequestWithItemsDto> result = new ArrayList<>();
        List<ItemWithRequestIdDto> itemWithRequestIdDtoList = new ArrayList<>();

        if (!itemRequestDtoList.isEmpty()) {
            Set<Integer> requestsId = itemRequestDtoList.stream().map(ItemRequestDto::getRequester).collect(Collectors.toSet());
            if (!requestsId.isEmpty()) {
                itemWithRequestIdDtoList.addAll(itemRepository.getItemsWithRequestDtoList(requestsId));
            }
        }

        for (ItemRequestDto i: itemRequestDtoList) {
            List<ItemWithRequestIdDto> list = new ArrayList<>();
            for (int j = 0; j < itemWithRequestIdDtoList.size(); j++) {
                if (Objects.equals(i.getId(), itemWithRequestIdDtoList.get(j).getRequestId())) {
                    list.add(itemWithRequestIdDtoList.get(j));
                    itemWithRequestIdDtoList.remove(j);
                    j--;
// Обе коллекции отсортированы от новых к старым объектам. Вложенный цикл прекращает работу как только RequestId перестают совпадать
        // и каждый раз при совпадении элемент из коллекции вложенного цикла - удаляется.
                    // Всё для того чтобы не было повторных проходов.
                } else {
                    break;
                }
            }
            result.add(requestMapper.toItemRequestWithItemsDto(i, list));
        }
        return result;
    }
}
