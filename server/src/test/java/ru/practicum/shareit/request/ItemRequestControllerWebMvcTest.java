package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@WebMvcTest
class ItemRequestControllerWebMvcTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;
    private String created;
    private ItemRequestDto itemRequestDto;
    private ItemRequestWithItemsDto itemRequestWithItemsDto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate localDate = now.toLocalDate();
        LocalTime localTime = LocalTime.parse(now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        LocalDateTime ldt = LocalDateTime.of(localDate, localTime);

        // String created = localDate + " " + localTime  -  нужна так как если в
        // .andExpect(MockMvcResultMatchers.jsonPath("$.created").value(created))
        // поместить объект LocalDateTime, а не собранную строку - то возникает ошибка из-за буквы Т между датой и временем:
        // java.lang.AssertionError: JSON path "$.created" expected:<2023-06-09T12:45:38> but was:<2023-06-09 12:45:38>

        created = localDate + " " + localTime;
        itemRequestDto = new ItemRequestDto(0, "Need some item", ldt, 1);
        ItemWithRequestIdDto itemWithRequestIdDto = new ItemWithRequestIdDto(0, "itemName",
                "itemDescription", true, 1);
        itemRequestWithItemsDto = new ItemRequestWithItemsDto(0, "Need some item", ldt, 1,
                List.of(itemWithRequestIdDto));
    }

    @SneakyThrows
    @Test
    void addRequest_whenEverythingIsOk_whenReturnOK() {
        when(itemRequestService.addItemRequest(itemRequestDto.getRequester(), itemRequestDto)).thenReturn(itemRequestDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Need some item"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created").value(created))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requester").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
        verify(itemRequestService, times(1)).addItemRequest(itemRequestDto.getRequester(), itemRequestDto);
    }

    @SneakyThrows
    @Test
    void getItemRequestsList() {
        List<ItemRequestWithItemsDto> itemRequestWithItemsDtoList = List.of(itemRequestWithItemsDto);
        when(itemRequestService.getItemRequestsList(1)).thenReturn(itemRequestWithItemsDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Need some item"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].created").value(created))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].requester").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].items.size()").value(1));

        verify(itemRequestService, times(1)).getItemRequestsList(1);
    }

    @SneakyThrows
    @Test
    void getItemRequestById() {
        Integer requestId = 0;
        when(itemRequestService.getItemRequestById(1, requestId)).thenReturn(itemRequestWithItemsDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{id}", requestId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Need some item"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created").value(created))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requester").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.size()").value(1));

        verify(itemRequestService, times(1)).getItemRequestById(1, requestId);
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsList_whenAllParamsExists_thenReturnList() {
        List<ItemRequestWithItemsDto> itemRequestWithItemsDtoList = List.of(itemRequestWithItemsDto);
        when(itemRequestService.getAllItemRequestsList(1, 0, 1)).thenReturn(itemRequestWithItemsDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Need some item"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].created").value(created))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].requester").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].items.size()").value(1));

        verify(itemRequestService, times(1)).getAllItemRequestsList(1, 0, 1);
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsList_whenThereAreNoParams_thenReturnList() {
        List<ItemRequestWithItemsDto> itemRequestWithItemsDtoList = List.of(itemRequestWithItemsDto);
        when(itemRequestService.getAllItemRequestsList(1, null, null)).thenReturn(itemRequestWithItemsDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Need some item"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].created").value(created))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].requester").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].items.size()").value(1));

        verify(itemRequestService, times(1)).getAllItemRequestsList(1, null, null);
    }
}