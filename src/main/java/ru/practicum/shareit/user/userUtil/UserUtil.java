package ru.practicum.shareit.user.userUtil;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserUtil {
    public User makeUser(User oldUser, UserDto userDto) {
        return User.builder()
                .id(oldUser.getId())
                .name(userDto.getName() == null ? oldUser.getName() : userDto.getName())
                .email(userDto.getEmail() == null ? oldUser.getEmail() : userDto.getEmail())
                .build();
    }
}
