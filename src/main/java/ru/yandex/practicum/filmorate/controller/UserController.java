package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User newUser) throws ConditionsNotMetException, DuplicatedDataException {
        validate(newUser);

        newUser.setId(getNextId());
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        return users.put(newUser.getId(), newUser);
    }

    @PutMapping
    public User update(@RequestBody User newUser) throws ConditionsNotMetException, DuplicatedDataException {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        validate(newUser);
        return users.replace(newUser.getId(), newUser);
    }

    private void validate(User newUser) throws ConditionsNotMetException, DuplicatedDataException {
        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            String s = "Имейл должен быть указан";
            log.info("Вызвано исключение: " + s + " Пришло: " + newUser.getEmail());
            throw new ConditionsNotMetException(s);
        }
        if (newUser.getEmail().indexOf('@') < 0) {
            String s = "Имейл должен содержать символ @";
            log.info("Вызвано исключение: " + s + " Пришло: " + newUser.getEmail());
            throw new ConditionsNotMetException(s);
        }
        if (findEmail(newUser)) {
            String s = "Этот имейл уже используется";
            log.info("Вызвано исключение: " + s + " Пришло: " + newUser.getEmail());
            throw new DuplicatedDataException(s);
        }
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            String s = "Дата рождения не может быть в будущем";
            log.info("Вызвано исключение: " + s + " Пришло: " + newUser.getBirthday());
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
    }

    private boolean findEmail(User newUser) {
        for (User user : users.values()) {
            if (user.getEmail().equals(newUser.getEmail())) {
                return true;
            }
        }
        return false;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
