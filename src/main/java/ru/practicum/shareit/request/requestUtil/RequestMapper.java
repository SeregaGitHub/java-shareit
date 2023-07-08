package ru.practicum.shareit.request.requestUtil;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public class RequestMapper {
    private final ModelMapper modelMapper;

    public RequestMapper() {
        this.modelMapper = new ModelMapper();
        Configuration configuration = modelMapper.getConfiguration();
        configuration.setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        modelMapper.typeMap(ItemRequest.class, ItemRequestDto.class)
                .addMappings(m -> m.skip(ItemRequestDto::setRequester));
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = modelMapper.map(itemRequestDto, ItemRequest.class);
        itemRequest.setRequester(user);
        return itemRequest;
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = modelMapper.map(itemRequest, ItemRequestDto.class);
        itemRequestDto.setRequester(itemRequest.getRequester().getId());
        return itemRequestDto;
    }

    public ItemRequestWithItemsDto toItemRequestWithItemsDto(ItemRequestDto itemRequestDto, List<ItemWithRequestIdDto> list) {
        ItemRequestWithItemsDto itemRequestWithItemsDto = modelMapper.map(itemRequestDto, ItemRequestWithItemsDto.class);
        itemRequestWithItemsDto.setItems(list);
        return itemRequestWithItemsDto;
    }
}
