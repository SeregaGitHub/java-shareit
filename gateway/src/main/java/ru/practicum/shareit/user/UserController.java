package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.mark.Create;
import ru.practicum.shareit.util.mark.Update;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Getting all users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") Integer id) {
        log.info("Getting user with Id={}", id);
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Adding new user");
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") Integer id,
                                             @RequestBody @Validated(Update.class) UserDto userDto) {
        log.info("Updating user with Id={}", id);
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Integer id) {
        log.info("Deleting user with Id={}", id);
        return userClient.deleteUser(id);
    }
}
