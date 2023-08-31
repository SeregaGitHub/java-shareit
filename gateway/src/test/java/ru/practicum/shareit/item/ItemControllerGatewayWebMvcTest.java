package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.user.UserClient;

import java.time.LocalDateTime;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@WebMvcTest
class ItemControllerGatewayWebMvcTest {
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
    private static final String URL_ITEMS = "/items";

    @SneakyThrows
    @Test
    void addItem_whenDescriptionIsEmpty_thenReturnBadRequest() {
        ItemWithRequestDto itemWithRequestDtoEmptyDescription = new ItemWithRequestDto(1, "itemName",
                "", true, null);

        mockMvc.perform(MockMvcRequestBuilders.post(URL_ITEMS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .content(objectMapper.writeValueAsString(itemWithRequestDtoEmptyDescription)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(itemClient, never()).addItem(2, itemWithRequestDtoEmptyDescription);
    }

    @SneakyThrows
    @Test
    void updateItem_whenItemNameIsToMatch_thenReturnBadRequest() {
        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("_____updateItem_whenItemNameIsToMatch_thenReturnBadRequest()_____")
                .description("newItemDescription")
                .available(false)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.patch(URL_ITEMS + "/{id}", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(itemClient, never()).updateItem(2, itemDto, itemDto.getId());
    }

    @SneakyThrows
    @Test
    void addComment_whenCommentHaveEmptyText_whenReturnBadRequest() {
        CommentDto commentDtoWithEmptyText = new CommentDto(0, "", "author",
                LocalDateTime.now());

        mockMvc.perform(MockMvcRequestBuilders.post(URL_ITEMS + "/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .content(objectMapper.writeValueAsString(commentDtoWithEmptyText)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(itemClient, never()).addComment(2, 1, commentDtoWithEmptyText);
    }
}