package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    List<User> getAllUsers();

    User getUser(Integer id);

    User addUser(User user);

    User updateUser(Integer id, Map<String, Object> userFields);

    User deleteUser(Integer id);
}
