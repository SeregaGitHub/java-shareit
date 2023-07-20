package ru.practicum.shareit.item.jsonTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDto> jsonTester;
    private Integer id;
    private String text;
    private String authorName;
    private LocalDateTime created;

    @BeforeEach
    void beforeEach() {
        id = 1;
        text = "textTest";
        authorName = "authorNameTest";
        created = LocalDateTime.of(2023, 10,10,12, 12, 12, 123456);
    }

    @Test
    void serializeCommentDtoFields() throws IOException {
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("textTest")
                .authorName("authorNameTest")
                .created(LocalDateTime.of(2023, 10,10,12, 12, 12, 123456))
                .build();

        JsonContent<CommentDto> result = jsonTester.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(text);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(authorName);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.toString());
    }

    @Test
    void deserializeCommentDtoFields() throws IOException {
        String jsonContent = String.format("{\"id\": \"%d\", " +
                                            "\"text\": \"%s\", " +
                                            "\"authorName\": \"%s\", " +
                                            "\"created\": \"%s\"}", id, text, authorName, created);
        CommentDto result = jsonTester.parse(jsonContent).getObject();

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getText()).isEqualTo(text);
        assertThat(result.getAuthorName()).isEqualTo(authorName);
        assertThat(result.getCreated()).isEqualTo(created);
    }
}
