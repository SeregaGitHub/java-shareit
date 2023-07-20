package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerGatewayTest {
    @Mock
    private UserClient userClient;
    @InjectMocks
    private UserController userController;
    private UserDto userDto;
    private final String URL_USERS = "/users";

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1)
                .name("name")
                .email("email@yandex.ru")
                .build();
    }

    @Test
    void getAllUsers() {
        List<UserDto> expectedUsers = List.of(UserDto.builder()
                .id(1)
                .name("name")
                .email("email@yandex.ru")
                .build());
        when(userClient.getAllUsers()).thenReturn(ResponseEntity.created(URI.create(URL_USERS)).body(List.of(userDto)));

        ResponseEntity<Object> response = userController.getAllUsers();
        List<Object> list = (List<Object>) response.getBody();

        assertEquals(expectedUsers, list);

        verify(userClient,times(1)).getAllUsers();
    }

    @Test
    void getUser() {
        when(userClient.getUser(1)).thenReturn(ResponseEntity.created(URI.create(URL_USERS)).body(userDto));

        ResponseEntity<Object> response = userController.getUser(1);
        UserDto returnedUser = (UserDto) response.getBody();

        assertEquals(userDto, returnedUser);
        verify(userClient, times(1)).getUser(1);
    }

    @Test
    void addUser() {
        when(userClient.addUser(userDto)).thenReturn(ResponseEntity.created(URI.create(URL_USERS)).body(userDto));

        ResponseEntity<Object> response = userController.addUser(userDto);
        UserDto returnedUser = (UserDto) response.getBody();

        assertEquals(userDto, returnedUser);
        verify(userClient, times(1)).addUser(userDto);
    }

    @Test
    void updateUser() {
        when(userClient.updateUser(1, userDto)).thenReturn(ResponseEntity.created(URI.create(URL_USERS)).body(userDto));

        ResponseEntity<Object> response = userController.updateUser(1, userDto);
        UserDto returnedUser = (UserDto) response.getBody();

        assertEquals(userDto, returnedUser);

        verify(userClient, times(1)).updateUser(1, userDto);
    }

    @Test
    void deleteUser() {
        when(userClient.deleteUser(1)).thenReturn(ResponseEntity.created(URI.create(URL_USERS)).body(userDto));

        ResponseEntity<Object> response = userController.deleteUser(1);
        UserDto returnedUser = (UserDto) response.getBody();

        assertEquals(userDto, returnedUser);
        verify(userClient, times(1)).deleteUser(1);
    }
}