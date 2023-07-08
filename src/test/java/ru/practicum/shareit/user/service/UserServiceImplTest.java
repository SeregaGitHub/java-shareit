package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.userUtil.UserMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    private User user;
    private User updatedUser;

    @BeforeEach
    void beforeEach() {
        user = new User(0, "name", "user@yandex.ru");
        updatedUser = new User(0, "newName", "newEmail@yandex.ru");
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> returnedList = userServiceImpl.getAllUsers();

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUser_whenUserNotFound_thenThrowNewNotFoundException() {
        Integer userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(
                NotFoundException.class, () -> userServiceImpl.getUser(userId));

        assertEquals("User with Id=" + userId + " - does not exist", notFoundException.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUser_whenUserFound_thenReturnUser() {
        Integer userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User returnedUser = userServiceImpl.getUser(userId);

        assertEquals(user, returnedUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void addUser() {
        when(userRepository.save(user)).thenReturn(user);

        UserDto returnedUser = userServiceImpl.addUser(UserMapper.toUserDto(user));

        assertEquals(UserMapper.toUserDto(user), returnedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_whenUserNotFound_thenThrowNewNotFoundException() {
        Integer userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userServiceImpl.updateUser(userId, UserMapper.toUserDto(updatedUser)));

        assertEquals("User with Id=" + userId + " - does not exist", notFoundException.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(user);
    }

    @Test
    void updateUser_whenUserFound_thenReturnUser() {
        Integer userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userServiceImpl.updateUser(userId, UserMapper.toUserDto(updatedUser));

        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        User captorUser = userArgumentCaptor.getValue();
        assertEquals(userId, captorUser.getId());
        assertEquals(updatedUser.getName(), captorUser.getName());
        assertEquals(updatedUser.getEmail(), captorUser.getEmail());
    }

    @Test
    void deleteUser() {
        Integer userId = user.getId();
        doNothing().when(userRepository).deleteById(userId);

        userRepository.deleteById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}