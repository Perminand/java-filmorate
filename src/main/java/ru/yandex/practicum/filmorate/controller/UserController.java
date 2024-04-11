package ru.yandex.practicum.filmorate.controller;

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
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User newUser) throws ConditionsNotMetException, DuplicatedDataException {
        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (newUser.getEmail().indexOf('@') < 0) {
            throw new ConditionsNotMetException("Имейл должен содержать символ @");
        }
        if (findEmail(newUser)) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
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
        if (newUser.getEmail() != null) {
            if (newUser.getEmail().indexOf('@') < 0) {
                throw new ConditionsNotMetException("Имейл должен содержать символ @");
            }
            if (findEmail(newUser)) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
        }
        return users.replace(newUser.getId(), newUser);
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
