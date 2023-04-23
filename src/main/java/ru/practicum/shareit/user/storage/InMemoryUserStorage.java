package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Utilities;

import java.lang.reflect.Field;
import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private static Integer userId = 0;
    private final Map<Integer, User> users = new HashMap<>();
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Integer id) {
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        Utilities.checkDuplicateEmail(user.getEmail(), users);
        user.setId(++userId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Integer id, Map<String, Object> userFields) {
        Optional<Map.Entry<String, Object>> opt = userFields.entrySet()
                .stream()
                .filter(v -> v.getKey().equals("email"))
                .findFirst();
        String checkEmail;
        if (opt.isPresent()) {
            checkEmail = (String) opt.get().getValue();
            Utilities.checkDuplicateEmailWhenUpdating(checkEmail, users, id);
        }

        User user = getUser(id);
        if (user == null) {
            log.warn("User with Id={} - does not exist", id);
            throw new UserNotFoundException("User with Id=" + id + " - does not exist");
        } else {
            userFields.forEach((k, v) -> {
                Field field = ReflectionUtils.findField(User.class, k);
                assert field != null;
                field.setAccessible(true);
                ReflectionUtils.setField(field, user, v);
            });
        }
        log.info("User with Id={} was updated", id);
        users.put(id, user);
        return user;
    }

    @Override
    public User deleteUser(Integer id) {
        return users.remove(id);
    }
}
