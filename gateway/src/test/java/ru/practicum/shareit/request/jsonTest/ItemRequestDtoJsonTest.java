package ru.practicum.shareit.request.jsonTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jsonTester;
    private Integer id;
    private String description;
    private final LocalDateTime created = LocalDateTime.of(
            2023, 10, 10, 12, 12, 12);
    private Integer requester;

    @BeforeEach
    void beforeEach() {
        id = 1;
        description = "descriptionTest";
        requester = 2;
    }

    @Test
    void serializeItemRequestDtoFields() throws IOException {
        ItemRequestDto itemDto = ItemRequestDto.builder()
                .id(1)
                .description("descriptionTest")
                .created(LocalDateTime.of(
                        2023, 10, 10, 12, 12, 12))
                .requester(2)
                .build();

        JsonContent<ItemRequestDto> result = jsonTester.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(description);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(
                created.toString().replace('T', ' '));
        assertThat(result).extractingJsonPathNumberValue("$.requester").isEqualTo(2);
    }

    @Test
    void deserializeItemRequestDtoFields() throws IOException {
        String jsonContent = String.format("{\"id\": \"%d\", " +
                                            "\"description\": \"%s\", " +
                                            "\"created\": \"%s\", " +
                                            "\"requester\": \"%d\"}",
                                            id, description, created.toString().replace('T', ' '), requester);
        ItemRequestDto result = jsonTester.parse(jsonContent).getObject();

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getCreated()).isEqualTo(created);
        assertThat(result.getRequester()).isEqualTo(requester);
    }
}
