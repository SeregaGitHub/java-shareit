package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest
class ItemControllerWebMvcTest {
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
    private User owner;
    private ItemWithRequestDto itemWithRequestDto;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private LocalDateTime ldt;
    private ItemWithBookingDto itemWithBookingDto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate localDate = now.toLocalDate();
        LocalTime localTime = LocalTime.parse(now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        ldt = LocalDateTime.of(localDate, localTime);

        owner = new User(1, "name", "email@yandex.ru");
        itemWithRequestDto = new ItemWithRequestDto(0, "itemName",
                "itemDescription", true, null);
        commentDto = new CommentDto(0, "someComment", "author", ldt);
        itemDto = new ItemDto(0, "itemName", "itemDescription", true);
        itemWithBookingDto = new ItemWithBookingDto(0, "itemName", "itemDescription", true,
                null, null, List.of(commentDto));
    }

    @SneakyThrows
    @Test
    void addItem_whenRequestIdExist_thenReturnItemWithRequestDto() {
        ItemWithRequestDto itemWithRequestDto = new ItemWithRequestDto(0, "itemName",
                "itemDescription", true, 1);
        when(itemService.addItem(owner.getId(), itemWithRequestDto)).thenReturn(itemWithRequestDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemWithRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("itemName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("itemDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemWithRequestDto), result);
        verify(itemService, times(1)).addItem(owner.getId(), itemWithRequestDto);
    }

    @SneakyThrows
    @Test
    void addItem_whenRequestIdDoesNotExist_thenReturnItemWithRequestDto() {
        when(itemService.addItem(owner.getId(), itemWithRequestDto)).thenReturn(itemWithRequestDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemWithRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("itemName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("itemDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemWithRequestDto), result);
        verify(itemService, times(1)).addItem(owner.getId(), itemWithRequestDto);
    }

    @SneakyThrows
    @Test
    void updateItem_whenItemFound_thenUpdateSomeFields() {
        ItemDto itemDto = ItemDto.builder()
                .id(0)
                .name("newItemName")
                .description("newItemDescription")
                .available(false)
                .build();
        when(itemService.updateItem(owner.getId(), itemDto, itemDto.getId())).thenReturn(itemDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.patch("/items/{id}", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("newItemName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("newItemDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
        verify(itemService, times(1)).updateItem(owner.getId(), itemDto, itemDto.getId());
    }

    @SneakyThrows
    @Test
    void getItem() {
        Integer itemId = 0;
        when(itemService.getItem(1, itemId)).thenReturn(itemWithBookingDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{id}", itemId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("itemName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("itemDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comments.size()").value(1));

        verify(itemService, times(1)).getItem(1, itemId);
    }

    @SneakyThrows
    @Test
    void getItems_whenAllParamsExists_thenReturnList() {
        when(itemService.getItems(1, 0, 1)).thenReturn(List.of(itemWithBookingDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1)))

                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("itemName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("itemDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].comments.size()").value(1));

        verify(itemService, times(1)).getItems(1, 0, 1);
    }

    @SneakyThrows
    @Test
    void getItems_whenThereAreNoParams_thenReturnList() {
        when(itemService.getItems(1, null, null)).thenReturn(List.of(itemWithBookingDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1)))

                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("itemName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("itemDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].comments.size()").value(1));

        verify(itemService, times(1)).getItems(1, null, null);
    }

    @SneakyThrows
    @Test
    void getItemsBySearch_whenThereAreNoParams_thenReturnList() {
        when(itemService.getItemsBySearch("itemN", null, null)).thenReturn(List.of(itemDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "itemN"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("itemName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("itemDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(true));

        verify(itemService, times(1)).getItemsBySearch("itemN", null, null);
    }

    @SneakyThrows
    @Test
    void getItemsBySearch_whenAllParamsExists_thenReturnList() {
        when(itemService.getItemsBySearch("itemN", 0, 1)).thenReturn(List.of(itemDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "itemN")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("itemName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("itemDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(true));

        verify(itemService, times(1)).getItemsBySearch("itemN", 0, 1);
    }

    @SneakyThrows
    @Test
    void addComment_whenEverythingIsOk_whenReturnOK() {
        when(itemService.addComment(owner.getId(), itemWithRequestDto.getId(), commentDto)).thenReturn(commentDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", itemWithRequestDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text").value("someComment"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorName").value("author"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created").value(ldt.toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
        verify(itemService, times(1)).addComment(owner.getId(), itemWithRequestDto.getId(), commentDto);
    }
}