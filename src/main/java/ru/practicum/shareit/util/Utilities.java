package ru.practicum.shareit.util;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserEmailHaveDuplicate;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

@Component
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
}
