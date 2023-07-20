package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.net.URI;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest
class UserControllerGatewayWebMvcTest {
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
    private static final String URL_USERS = "/users";

    private final UserDto userDto = UserDto.builder()
            .id(1)
            .name("name")
            .email("email@yandex.ru")
            .build();

    @SneakyThrows
    @Test
    void getAllUsers() {
        when(userClient.getAllUsers()).thenReturn(ResponseEntity.created(URI.create(URL_USERS)).body(List.of(userDto)));

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1)))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("name"))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("email@yandex.ru"));

        verify(userClient,times(1)).getAllUsers();
    }

    @SneakyThrows
    @Test
    void getUser_whenEverythingIsOk_whenReturnOK() {
        Integer userId = 0;
        when(userClient.getUser(userId)).thenReturn(ResponseEntity.created(URI.create(URL_USERS)).body(userDto));

        mockMvc.perform(MockMvcRequestBuilders.get(URL_USERS + "/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("email@yandex.ru"));

        verify(userClient, times(1)).getUser(userId);
    }

    @SneakyThrows
    @Test
    void addUser_whenNameIsNotValid_thenReturnBadRequest() {
        UserDto userDtoWithNotValidName = UserDto.builder()
                .id(1)
                .name("")
                .email("email@yandex.ru")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(URL_USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoWithNotValidName)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(userClient, never()).addUser(userDto);
    }

    @SneakyThrows
    @Test
    void addUser_whenEverythingIsOk_whenReturnOK() {
        when(userClient.addUser(userDto)).thenReturn(ResponseEntity.created(URI.create(URL_USERS)).body(userDto));

        String result = mockMvc.perform(MockMvcRequestBuilders.post(URL_USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("email@yandex.ru"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userClient, times(1)).addUser(userDto);
    }

    @SneakyThrows
    @Test
    void updateUser_whenEmailIsNotValid_thenReturnBadRequest() {
        UserDto userWithNotValidEmail = UserDto.builder()
                .id(1)
                .name("name")
                .email("email.yandex.ru")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.patch(URL_USERS + "/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithNotValidEmail)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(userClient, never()).updateUser(1, userWithNotValidEmail);
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserFound_thenUpdateSomeFields() {
        when(userClient.updateUser(1, userDto)).thenReturn(ResponseEntity.created(URI.create(URL_USERS)).body(userDto));

        String result = mockMvc.perform(MockMvcRequestBuilders.patch(URL_USERS + "/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("email@yandex.ru"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userClient, times(1)).updateUser(1, userDto);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        mockMvc.perform(MockMvcRequestBuilders.delete(URL_USERS + "/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(userClient, times(1)).deleteUser(1);
    }
}