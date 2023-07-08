package ru.practicum.shareit.user.userUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private User user;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
         user = User.builder()
                 .id(1)
                 .name("name")
                 .email("user@yandex.ru")
                 .build();
         userDto = UserDto.builder()
                 .id(1)
                 .name("name")
                 .email("user@yandex.ru")
                 .build();
    }

    @Test
    void toUserDto() {
        UserDto userDtoCheck = UserMapper.toUserDto(user);

        assertEquals(userDto, userDtoCheck);
    }

    @Test
    void toUser() {
        User userCheck = UserMapper.toUser(userDto);

        assertEquals(user, userCheck);
    }
}