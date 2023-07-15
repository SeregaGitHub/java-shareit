package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userUtil.UserMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest
class UserControllerWebMvcTest {
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
    private final User userWithGoodFields = new User(0, "name", "email@yandex.ru");

    @SneakyThrows
    @Test
    void getAllUsers() {
        when(userService.getAllUsers()).thenReturn(List.of(userWithGoodFields));

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("email@yandex.ru"));

        verify(userService,times(1)).getAllUsers();
    }

    @SneakyThrows
    @Test
    void getUser_whenEverythingIsOk_whenReturnOK() {
        Integer userId = 0;
        when(userService.getUser(userId)).thenReturn(userWithGoodFields);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("email@yandex.ru"));

        verify(userService, times(1)).getUser(userId);
    }

    @SneakyThrows
    @Test
    void addUser_whenNameIsNotValid_thenReturnBadRequest() {
        User userWithNotValidName = new User(0, "", "email@yandex.ru");
        UserDto userDto = UserMapper.toUserDto(userWithNotValidName);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(userService, never()).addUser(userDto);
    }

    @SneakyThrows
    @Test
    void addUser_whenEverythingIsOk_whenReturnOK() {
        UserDto userDto = UserMapper.toUserDto(userWithGoodFields);
        when(userService.addUser(userDto)).thenReturn(userDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("email@yandex.ru"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService, times(1)).addUser(userDto);
    }

    @SneakyThrows
    @Test
    void updateUser_whenEmailIsNotValid_thenReturnBadRequest() {
        Integer userId = 0;
        User userWithNotValidEmail = new User(0, "name", "email.yandex.ru");
        UserDto userDto = UserMapper.toUserDto(userWithNotValidEmail);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(userService, never()).updateUser(userId, userDto);
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserFound_thenUpdateSomeFields() {
        Integer userId = 0;
        UserDto userToUpdateDto = UserMapper.toUserDto(userWithGoodFields);
        when(userService.updateUser(userId, userToUpdateDto)).thenReturn(userToUpdateDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToUpdateDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("email@yandex.ru"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userToUpdateDto), result);
        verify(userService, times(1)).updateUser(userId, userToUpdateDto);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        Integer userId = 0;
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(userService, times(1)).deleteUser(userId);
    }
}