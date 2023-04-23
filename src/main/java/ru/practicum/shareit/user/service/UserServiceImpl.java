package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUser(Integer id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            log.warn("User with Id={} - does not exist", id);
            throw new UserNotFoundException("User with Id=" + id + " - does not exist");
        } else {
            return user;
        }
    }

    @Override
    public User addUser(@Valid User user) {
        log.info("User with Id={} was added", user.getId());
        userStorage.addUser(user);
        return user;
    }

    @Override
    public User updateUser(Integer id, Map<String, Object> userFields) {
        return userStorage.updateUser(id, userFields);
    }

    @Override
    public void deleteUser(Integer id) {
        User user = userStorage.deleteUser(id);
        if (user == null) {
            log.warn("User with Id={} - does not exist", id);
            throw new UserNotFoundException("User with Id=" + id + " - does not exist");
        } else {
            log.info("User with Id={} - was deleted", id);
        }
    }
}
