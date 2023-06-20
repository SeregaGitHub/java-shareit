package ru.practicum.shareit.user.userUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserUtilTest {
    private User oldUser;
    private UserDto userAllFieldsDto;
    private UserDto userPartFieldsDto;

    @BeforeEach
    void beforeEach() {
        oldUser = User.builder()
                .id(1)
                .name("oldUserName")
                .email("oldUser@yandex.ru")
                .build();
        userAllFieldsDto = UserDto.builder()
                .id(1)
                .name("updatedUserName")
                .email("updatedUser@yandex.ru")
                .build();
        userPartFieldsDto = UserDto.builder()
                .id(1)
                .name("updatedUserName")
                .build();
    }

    @Test
    void makeUser_whenAllFieldsMustBeUpdated_whenReturnUpdatedUser() {
        User returnedUser = UserUtil.makeUser(oldUser, userAllFieldsDto);

        assertEquals(userAllFieldsDto.getName(), returnedUser.getName());
        assertEquals(userAllFieldsDto.getEmail(), returnedUser.getEmail());
    }

    @Test
    void makeUser_whenPartOfTheFieldsMustBeUpdated_whenReturnUpdatedUser() {
        User returnedUser = UserUtil.makeUser(oldUser, userPartFieldsDto);

        assertEquals(userPartFieldsDto.getName(), returnedUser.getName());
        assertEquals(oldUser.getEmail(), returnedUser.getEmail());
    }
}