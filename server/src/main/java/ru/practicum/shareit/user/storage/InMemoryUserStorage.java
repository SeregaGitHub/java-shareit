package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.userUtil.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Utilities;

import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private Integer userId = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Integer id) {
        return users.get(id);
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        Utilities.checkDuplicateEmail(userDto.getEmail(), users);
        userDto.setId(++userId);
        users.put(userDto.getId(), UserMapper.toUser(userDto));
        return userDto;
    }

    @Override
    public UserDto updateUser(Integer id, UserDto updatedUser) {
        User user = getUser(id);
        if (user == null) {
            log.warn("User with Id={} - does not exist", id);
            throw new NotFoundException("User with Id=" + id + " - does not exist");
        } else {
            Optional<String> opt = Optional.ofNullable(updatedUser.getEmail());
            String checkEmail;
            if (opt.isPresent()) {
                checkEmail = opt.get();
                Utilities.checkDuplicateEmailWhenUpdating(checkEmail, users, id);
            }

            user = makeUser(user, updatedUser);
            users.put(user.getId(), user);
            log.info("User with Id={} was updated", id);
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public User deleteUser(Integer id) {
        return users.remove(id);
    }

    private static User makeUser(User oldUser, UserDto userDto) {
        return User.builder()
                .id(oldUser.getId())
                .name(userDto.getName() == null ? oldUser.getName() : userDto.getName())
                .email(userDto.getEmail() == null ? oldUser.getEmail() : userDto.getEmail())
                .build();
    }
}
