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
class UserHibernateServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserHibernateService userHibernateService;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User(0, "name", "user@yandex.ru");
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> returnedList = userHibernateService.getAllUsers();

        assertEquals(1, returnedList.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUser_whenUserNotFound_thenThrowNewNotFoundException() {
        Integer userId = user.getId();
        when(userRepository.findById(userId)).thenThrow(
                new NotFoundException("NotFoundException"));

        assertThrows(NotFoundException.class, () -> userHibernateService.getUser(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUser_whenUserFound_thenReturnUser() {
        Integer userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User returnedUser = userHibernateService.getUser(userId);

        assertEquals(user, returnedUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void addUser() {
        when(userRepository.save(user)).thenReturn(user);

        UserDto returnedUser = userHibernateService.addUser(UserMapper.toUserDto(user));

        assertEquals(UserMapper.toUserDto(user), returnedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_whenUserNotFound_thenThrowNewNotFoundException() {
        Integer userId = user.getId();
        when(userRepository.findById(userId)).thenThrow(new NotFoundException("NotFoundException"));

        assertThrows(NotFoundException.class, () -> userHibernateService.updateUser(userId, UserMapper.toUserDto(user)));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(user);
    }

    @Test
    void updateUser_whenUserFound_thenReturnUser() {
        Integer userId = user.getId();
        User updatedUser = new User(userId, "newName", "newEmail@yandex.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userHibernateService.updateUser(userId, UserMapper.toUserDto(updatedUser));

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