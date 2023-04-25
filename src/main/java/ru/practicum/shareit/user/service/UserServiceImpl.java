package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

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
    public UserDto addUser(UserDto userDto) {
        log.info("User with name={} was added", userDto.getName());
        userStorage.addUser(userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(Integer id, UserDto userDto) {
        return userStorage.updateUser(id, userDto);
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
