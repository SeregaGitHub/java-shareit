package ru.practicum.shareit.user.jsonTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> jsonTester;
    private String name;
    private String email;

    @BeforeEach
    void beforeEach() {
        name = "testName";
        email = "testEmail@yandex.ru";
    }

    @Test
    void serializeUserDtoFields() throws IOException {
        UserDto userDto = UserDto.builder()
                .name("testName")
                .email("testEmail@yandex.ru")
                .build();

        JsonContent<UserDto> result = jsonTester.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(name);
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(email);
    }

    @Test
    void deserializeUserDtoFields() throws IOException {
        String jsonContent = String.format("{\"name\": \"%s\", \"email\": \"%s\"}", name, email);
        UserDto result = jsonTester.parse(jsonContent).getObject();

        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getEmail()).isEqualTo(email);
    }
}
