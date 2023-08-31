package ru.practicum.shareit.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookingErrorException;
import ru.practicum.shareit.exception.CommentErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

@WebMvcTest
class ErrorHandlerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private ItemService itemService;
    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void handleUserNotFoundTest() {
        when(userService.getUser(anyInt())).thenThrow(new NotFoundException("NotFoundException"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", anyInt())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @SneakyThrows
    @Test
    void handleTimeErrorTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(-1)
                .build();

        when(bookingService.addBooking(1, bookingDto)).thenThrow(new BookingErrorException("BookingErrorException"));

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void handleCommentErrorTest() {
        CommentDto commentDto = CommentDto.builder()
                .text("commentDto")
                .authorName("author")
                .build();

        when(itemService.addComment(1, 2, commentDto)).thenThrow(new CommentErrorException("CommentErrorException"));

        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", 2)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}