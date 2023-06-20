package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.userUtil.UserMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private InMemoryUserStorage inMemoryUserStorage;
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        user = new User(0, "name", "user@yandex.ru");
        userDto = UserDto.builder()
                .id(0)
                .name("name")
                .email("user@yandex.ru")
                .build();
    }

    @Test
    void getAllUsers() {
        when(inMemoryUserStorage.getAllUsers()).thenReturn(List.of(user));

        List<User> returnedList = userServiceImpl.getAllUsers();

        assertEquals(1, returnedList.size());
        verify(inMemoryUserStorage, times(1)).getAllUsers();
    }

    @Test
    void getUser_whenUserFound_thenReturnUser() {
        Integer userId = user.getId();
        when(inMemoryUserStorage.getUser(userId)).thenReturn(user);

        User returnedUser = userServiceImpl.getUser(userId);

        assertEquals(user, returnedUser);
        verify(inMemoryUserStorage, times(1)).getUser(userId);
    }

    @Test
    void addUser() {
        when(inMemoryUserStorage.addUser(userDto)).thenReturn(userDto);

        UserDto returnedUser = userServiceImpl.addUser(userDto);

        assertEquals(userDto, returnedUser);
        verify(inMemoryUserStorage, times(1)).addUser(userDto);
    }

    @Test
    void updateUser_whenUserNotFound_thenThrowNewNotFoundException() {
        Integer userId = user.getId();
        when(inMemoryUserStorage.getUser(userId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userServiceImpl.getUser(userId));

        verify(inMemoryUserStorage, times(1)).getUser(userId);
    }

    @Test
    void updateUser_whenUserFound_thenReturnUser() {
        Integer userId = user.getId();
        User updatedUser = new User(userId, "newName", "newEmail@yandex.ru");
        UserDto updatedUserDto = UserMapper.toUserDto(updatedUser);
        lenient().when(inMemoryUserStorage.getUser(userId)).thenReturn(user);

        userServiceImpl.updateUser(userId, userDto);

        assertEquals(updatedUser,
                ReflectionTestUtils.invokeMethod(InMemoryUserStorage.class, "makeUser", user, updatedUserDto));
    }

    @Test
    void deleteUser_whenUserNotFound_thenThrowException() {
        Integer userId = user.getId();
        when(inMemoryUserStorage.deleteUser(userId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userServiceImpl.deleteUser(userId));

        verify(inMemoryUserStorage, times(1)).deleteUser(userId);
    }

    @Test
    void deleteUser() {
        Integer userId = user.getId();
        when(inMemoryUserStorage.deleteUser(userId)).thenReturn(user);

        inMemoryUserStorage.deleteUser(userId);

        verify(inMemoryUserStorage, times(1)).deleteUser(userId);
    }
}