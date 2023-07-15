package ru.practicum.shareit.booking.jsonTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> jsonTester;
    private String start;
    private String end;
    private LocalDateTime now;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        start = dtf.format(LocalDateTime.now().plusHours(1));
        end = dtf.format(LocalDateTime.now().plusHours(2));
    }

    @Test
    void serializeLocalDateTimeInCorrectFormat() throws IOException {
        BookingDto bookingDto = BookingDto.builder()
                .start(now.plusHours(1))
                .end(now.plusHours(2))
                .itemId(1)
                .build();

        JsonContent<BookingDto> result = jsonTester.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start);
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}
