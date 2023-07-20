package ru.practicum.shareit.item.jsonTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> jsonTester;
    private Integer id;
    private String name;
    private String description;
    private Boolean available;

    @BeforeEach
    void beforeEach() {
        id = 1;
        name = "nameTest";
        description = "descriptionTest";
        available = true;
    }

    @Test
    void serializeItemDtoFields() throws IOException {
        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("nameTest")
                .description("descriptionTest")
                .available(true)
                .build();

        JsonContent<ItemDto> result = jsonTester.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(name);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(description);
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(available);
    }

    @Test
    void deserializeItemDtoFields() throws IOException {
        String jsonContent = String.format("{\"id\": \"%d\", " +
                                            "\"name\": \"%s\", " +
                                            "\"description\": \"%s\", " +
                                            "\"available\": \"%b\"}", id, name, description, available);
        ItemDto result = jsonTester.parse(jsonContent).getObject();

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getAvailable()).isEqualTo(available);
    }
}
