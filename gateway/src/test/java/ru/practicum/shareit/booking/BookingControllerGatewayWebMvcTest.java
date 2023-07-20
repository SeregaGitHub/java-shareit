package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.user.UserClient;

import java.time.LocalDateTime;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@WebMvcTest
class BookingControllerGatewayWebMvcTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserClient userClient;
    @MockBean
    private BookingClient bookingClient;
    @MockBean
    private ItemClient itemClient;
    @MockBean
    private ItemRequestClient itemRequestClient;
    private LocalDateTime now;
    private static final String URL_BOOKINGS = "/bookings";

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
    }

    @SneakyThrows
    @Test
    void addBooking_whenStartTimeIsInPast_thenThrowException() {
        BookItemRequestDto booking = new BookItemRequestDto(1, now.minusHours(1), now.plusHours(1), 3);

        mockMvc.perform(MockMvcRequestBuilders.post(URL_BOOKINGS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(bookingClient, never()).bookItem(2, booking);
    }

    @SneakyThrows
    @Test
    void updateBooking_whenEndTimeIsInPast_thenThrowException() {
        BookItemRequestDto booking = new BookItemRequestDto(1, now.plusHours(1), now.minusHours(1), 3);

        mockMvc.perform(MockMvcRequestBuilders.patch(URL_BOOKINGS + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(bookingClient, never()).bookItem(2, booking);
    }
}