package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserClient;

import java.time.LocalDateTime;
import static org.mockito.Mockito.*;

@WebMvcTest
class ItemRequestGatewayWebMvcControllerTest {
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
    private final String URL_REQUESTS = "/requests";

    @SneakyThrows
    @Test
    void addRequest_whenDescriptionIsNotValid_thenReturnBadRequest() {
        LocalDateTime ldt = LocalDateTime.parse(LocalDateTime.now().toString().substring(0, 19));
        ItemRequestDto itemRequestDto = new ItemRequestDto(0, "", ldt, 1);

        mockMvc.perform(MockMvcRequestBuilders.post(URL_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(itemRequestClient, never()).addItemRequest(1, itemRequestDto);
    }
}