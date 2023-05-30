package ru.practicum.shareit.user.userUtil;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserUtil {
    public static User makeUser(User oldUser, UserDto userDto) {
        return User.builder()
                .id(oldUser.getId())
                .name(userDto.getName() == null ? oldUser.getName() : userDto.getName())
                .email(userDto.getEmail() == null ? oldUser.getEmail() : userDto.getEmail())
                .build();
    }
}
