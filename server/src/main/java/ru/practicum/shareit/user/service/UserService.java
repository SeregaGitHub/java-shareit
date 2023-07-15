package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUser(Integer id);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Integer id, UserDto userDto);

    void deleteUser(Integer id);
}
