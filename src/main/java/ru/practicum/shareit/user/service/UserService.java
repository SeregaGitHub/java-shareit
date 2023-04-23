package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> getAllUsers();
    User getUser(Integer id);
    User addUser(@Valid User user);
    User updateUser(Integer id, Map<String, Object> userFields);
    void deleteUser(Integer id);
}
