package ru.practicum.shareit.booking.jsonTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<BookItemRequestDto> jsonTester;
    private Integer id;
    private final LocalDateTime start = LocalDateTime.of(
            2023, 10, 10, 12, 12, 12);
    private final LocalDateTime end = LocalDateTime.of(
            2023, 10, 11, 12, 12, 12);
    private int itemId;

    @BeforeEach
    void beforeEach() {
        id = 1;
        itemId = 2;
    }

    @Test
    void serializeItemRequestDtoFields() throws IOException {
        BookItemRequestDto itemDto = new BookItemRequestDto(
                1,
                LocalDateTime.of(
                2023, 10, 10, 12, 12, 12),
                LocalDateTime.of(
                        2023, 10, 11, 12, 12, 12),
                2
        );

        JsonContent<BookItemRequestDto> result = jsonTester.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
    }

    @Test
    void deserializeItemRequestDtoFields() throws IOException {
        String jsonContent = String.format("{\"id\": \"%d\", " +
                                            "\"start\": \"%s\", " +
                                            "\"end\": \"%s\", " +
                                            "\"itemId\": \"%d\"}",
                id, start, end, itemId);

        BookItemRequestDto result = jsonTester.parse(jsonContent).getObject();

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getStart()).isEqualTo(start);
        assertThat(result.getEnd()).isEqualTo(end);
        assertThat(result.getItemId()).isEqualTo(itemId);
    }
}
