package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.requestUtil.RequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRequestRepositoryTest {
    private User user;
    private User firstRequester;
    private User secondRequester;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void beforeEach() {
        RequestMapper requestMapper = new RequestMapper();
        user = User.builder()
                .name("name")
                .email("user@yandex.ru")
                .build();
        firstRequester = User.builder()
                .name("firstRequesterName")
                .email("firstRequester@yandex.ru")
                .build();
        secondRequester = User.builder()
                .name("secondRequesterName")
                .email("secondRequester@yandex.ru")
                .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("itemRequest")
                .created(LocalDateTime.now())
                .requester(firstRequester.getId())
                .build();

        userRepository.save(user);
        userRepository.save(firstRequester);
        userRepository.save(secondRequester);
        itemRequestRepository.save(requestMapper.toItemRequest(itemRequestDto, firstRequester));
        itemRequestRepository.save(requestMapper.toItemRequest(itemRequestDto, secondRequester));
    }

    @AfterEach
    void afterEach() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getItemRequestsOfUserList() {
        List<ItemRequestDto> returnedList = itemRequestRepository.getItemRequestsOfUserList(firstRequester.getId());

        assertEquals(1, returnedList.size());
    }

    @Test
    void getItemRequestsList() {
        List<ItemRequestDto> returnedList = itemRequestRepository.getItemRequestsList(user.getId());

        assertEquals(2, returnedList.size());
    }

    @Test
    void testGetItemRequestsList() {
        List<ItemRequestDto> returnedList = itemRequestRepository.getItemRequestsList(secondRequester.getId(),
                PageRequest.of(0, 1));

        assertEquals(1, returnedList.size());
    }
}