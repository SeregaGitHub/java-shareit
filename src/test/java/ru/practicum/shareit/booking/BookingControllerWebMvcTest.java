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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.bookingUtil.BookingMapper;
import ru.practicum.shareit.booking.bookingUtil.State;
import ru.practicum.shareit.booking.bookingUtil.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
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

@WebMvcTest
class BookingControllerWebMvcTest {
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
    private BookingDto bookingDto;
    private Booking booking;
    private User user;
    private User owner;


    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate localDate = now.toLocalDate();
        LocalTime localTime = LocalTime.parse(now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        LocalDateTime ldt = LocalDateTime.of(localDate, localTime);

        user = new User(0, "name", "email@yandex.ru");
        owner = new User(1, "owner", "owner@yandex.ru");
        User requester = new User(2, "requester", "requester@yandex.ru");
        ItemRequest itemRequest = new ItemRequest(0, "requestDescription", ldt, requester);
        Item item = new Item(0, "itemName", "itemDescription", true, owner, itemRequest);
        bookingDto = BookingDto.builder()
                .id(0)
                .start(ldt.plusHours(1))
                .end(ldt.plusHours(2))
                .itemId(item.getId())
                .build();
        booking = BookingMapper.toBooking(user, bookingDto, item);
    }

    @SneakyThrows
    @Test
    void addBooking() {
        Integer bookerId = booking.getBooker().getId();
        when(bookingService.addBooking(bookerId, bookingDto)).thenReturn(booking);

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookerId)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(booking), result);
        verify(bookingService, times(1)).addBooking(bookerId, bookingDto);
    }

    @SneakyThrows
    @Test
    void setNewStatus() {
        Booking bookingWithNewStatus = booking;
        bookingWithNewStatus.setStatus(Status.APPROVED);
        when(bookingService.setNewStatus(booking.getItem().getOwner().getId(), booking.getId(), true))
                .thenReturn(bookingWithNewStatus);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/{id}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booking.getItem().getOwner().getId())
                        .param("approved", String.valueOf(true))
                        .content(objectMapper.writeValueAsString(bookingWithNewStatus)))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.APPROVED.toString()));

        verify(bookingService, times(1))
                .setNewStatus(booking.getItem().getOwner().getId(), booking.getId(), true);
    }

    @SneakyThrows
    @Test
    void getBooking() {
        Integer bookerId = booking.getBooker().getId();
        Integer bookingId = bookingDto.getId();
        when(bookingService.getBooking(bookerId, bookingId)).thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{id}", bookingId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookerId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(bookingService, times(1)).getBooking(bookerId, bookingId);
    }

    @SneakyThrows
    @Test
    void getAllUserBooking_whenAllParamsExists_thenReturnList() {
        when(bookingService.getAllUserBookings(user.getId(), State.ALL.toString(), 0, 1)).thenReturn(List.of(booking));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId())
                        .param("state", String.valueOf(State.ALL))
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(bookingService, times(1)).getAllUserBookings(user.getId(), State.ALL.toString(), 0, 1);
    }

    @SneakyThrows
    @Test
    void getAllOwnerBooking_whenAllParamsExists_thenReturnList() {
        when(bookingService.getAllUserBookings(owner.getId(), State.ALL.toString(), 0, 1)).thenReturn(List.of(booking));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", String.valueOf(State.ALL))
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(bookingService, times(1)).getAllOwnerBooking(owner.getId(), State.ALL.toString(), 0, 1);
    }
}