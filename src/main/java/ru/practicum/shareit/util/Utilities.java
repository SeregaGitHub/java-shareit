package ru.practicum.shareit.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.exception.UserEmailHaveDuplicate;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Map;

@Component
@Slf4j
public class Utilities {
    public static void checkDuplicateEmail(String email, Map<Integer, User> userMap) {
        if (userMap.values().stream().anyMatch(v -> email.equals(v.getEmail()))) {
            throw new UserEmailHaveDuplicate("Such user email is already exists");
        }
    }

    public static void checkDuplicateEmailWhenUpdating(String email, Map<Integer, User> userMap, Integer id) {
        if (!email.equals(userMap.get(id).getEmail())) {
            checkDuplicateEmail(email, userMap);
        }
    }

    public static void checkUserExist(Integer user, UserStorage userStorage) {
        if (user == null) {
            log.warn("Request do not contain owner of the item");
            throw new OwnerNotFoundException("Request do not contain owner of the item");
        } else {
            if (userStorage.getUser(user) == null) {
                log.warn("User with Id={} - does not exist", user);
                throw new NotFoundException("User with Id=" + user + " - does not exist");
            }
        }
    }
}
