package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.userUtil.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.userUtil.UserUtil;

import java.util.List;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class UserHibernateService implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Integer id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User with Id=" + id + " - does not exist"));
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        log.info("User with name={} was added", userDto.getName());
        Integer userId = userRepository.save(UserMapper.toUser(userDto)).getId();
        userDto.setId(userId);
        return userDto;
    }

    @Override
    public UserDto updateUser(Integer id, UserDto userDto) {
        User user = getUser(id);
        if (user == null) {
            log.warn("User with Id={} - does not exist", id);
            throw new NotFoundException("User with Id=" + id + " - does not exist");
        } else {
            user = UserUtil.makeUser(user, userDto);
            userRepository.save(user);
            log.info("User with Id={} was updated", id);
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Integer id) {
        log.info("User with Id={} - was deleted", id);
        userRepository.deleteById(id);
    }
}
