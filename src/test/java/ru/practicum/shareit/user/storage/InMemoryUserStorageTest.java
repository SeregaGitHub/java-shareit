package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class InMemoryUserStorageTest {
    @InjectMocks
    private InMemoryUserStorage inMemoryUserStorage;
    private UserDto firstUser;
    private UserDto secondUser;
    private UserDto userDto;
    @BeforeEach
    void beforeEach() {
        firstUser = UserDto.builder()
                .name("firstUserName")
                .email("firstUser@yandex.ru")
                .build();
        secondUser = UserDto.builder()
                .name("secondUserName")
                .email("secondUser@yandex.ru")
                .build();
        userDto = UserDto.builder()
                .name("name")
                .email("user@yandex.ru")
                .build();

        inMemoryUserStorage.addUser(firstUser);
        inMemoryUserStorage.addUser(secondUser);
    }

    @AfterEach
    void afterEach() {
        inMemoryUserStorage.deleteUser(1);
        inMemoryUserStorage.deleteUser(2);
        ReflectionTestUtils.setField(inMemoryUserStorage, "userId", 0);
    }

    @Test
    void getAllUsers() {
        List<User> returnedList = inMemoryUserStorage.getAllUsers();

        assertEquals(2, returnedList.size());
    }

    @Test
    void getUser() {
        User returnedUser = inMemoryUserStorage.getUser(firstUser.getId());

        assertEquals(1, returnedUser.getId());
        assertEquals("firstUserName", returnedUser.getName());
        assertEquals("firstUser@yandex.ru", returnedUser.getEmail());
    }

    @Test
    void addUser() {
        UserDto returnedUser = inMemoryUserStorage.addUser(userDto);

        assertEquals(3, returnedUser.getId());
        assertEquals("name", returnedUser.getName());
        assertEquals("user@yandex.ru", returnedUser.getEmail());
    }

    @Test
    void updateUser() {
        Integer id = firstUser.getId();
        userDto.setId(id);
        UserDto returnedUser = inMemoryUserStorage.updateUser(id, userDto);

        assertEquals(id, returnedUser.getId());
        assertEquals("name", returnedUser.getName());
        assertEquals("user@yandex.ru", returnedUser.getEmail());
    }

    @Test
    void deleteUser() {
        User returnedUser = inMemoryUserStorage.deleteUser(firstUser.getId());

        assertEquals(firstUser.getId(), returnedUser.getId());
        assertEquals("firstUserName", returnedUser.getName());
        assertEquals("firstUser@yandex.ru", returnedUser.getEmail());
    }
}