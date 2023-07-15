package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userUtil.UserMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User(0, "name", "user@yandex.ru");
    }

    @Test
    void getAllUsers() {
        List<User> expectedUsers = List.of(new User());
        when(userService.getAllUsers()).thenReturn(expectedUsers);

        List<User> users = userController.getAllUsers();

        assertEquals(expectedUsers, users);
        verify(userService,times(1)).getAllUsers();
    }

    @Test
    void getUser() {
        Integer userId = user.getId();
        when(userService.getUser(userId)).thenReturn(user);

        User returnedUser = userController.getUser(userId);

        assertEquals(user, returnedUser);
        verify(userService, times(1)).getUser(userId);
    }

    @Test
    void addUser() {
        UserDto userDto = UserMapper.toUserDto(user);
        when(userService.addUser(userDto)).thenReturn(userDto);

        UserDto returnedUser = userController.addUser(userDto);

        assertEquals(userDto, returnedUser);
        verify(userService, times(1)).addUser(userDto);
    }

    @Test
    void updateUser() {
        Integer userId = user.getId();
        UserDto userDto = UserMapper.toUserDto(user);
        when(userService.updateUser(userId, userDto)).thenReturn(userDto);

        UserDto returnedUser = userController.updateUser(userId, userDto);

        assertEquals(userDto, returnedUser);
        verify(userService, times(1)).updateUser(userId, userDto);
    }

    @Test
    void deleteUser() {
        doNothing().when(userService).deleteUser(0);

        userController.deleteUser(0);

        verify(userService, times(1)).deleteUser(0);
    }
}