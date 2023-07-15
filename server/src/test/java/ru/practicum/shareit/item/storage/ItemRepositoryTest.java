package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {
    private Item item;
    private ItemRequest itemRequest;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .name("name")
                .email("user@yandex.ru")
                .build();
        User requester = User.builder()
                .name("requesterName")
                .email("requester@yandex.ru")
                .build();
        itemRequest = ItemRequest.builder()
                .description("itemN")
                .created(now)
                .requester(requester)
                .build();
        item = Item.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .itemRequest(itemRequest)
                .build();
        userRepository.save(user);
        userRepository.save(requester);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item);
    }

    @AfterEach
    void afterEach() {
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findItemByIdWithOwner() {
        Optional<Item> returnedItem = itemRepository.findItemByIdWithOwner(item.getId());

        assertTrue(returnedItem.isPresent());
    }

    @Test
    void getItemsWithRequestDtoList() {
        List<ItemWithRequestIdDto> returnedList = itemRepository.getItemsWithRequestDtoList(
                Set.of(itemRequest.getId()));

        assertEquals(1, returnedList.size());
    }
}