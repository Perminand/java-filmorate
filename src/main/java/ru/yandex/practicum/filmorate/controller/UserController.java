package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
    public User create(@RequestBody final User user) throws ValidationException, DuplicatedDataException {
        validate(user);
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.debug("User created: " + user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody final User user) throws ValidationException, DuplicatedDataException {
        if (user.getId() == null) {
            throw new ValidationException("id должен быть указан");
        }
        validate(user);
        users.replace(user.getId(), user);
        log.debug("User update: " + user);
        return user;
    }

    public void validate(final User newUser) throws ValidationException, DuplicatedDataException {
        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            final String s = "Имейл должен быть указан";
            log.info("Вызвано исключение: " + s + " Пришло: " + newUser.getEmail());
            throw new ValidationException(s);
        }
        if (newUser.getEmail().indexOf('@') < 0) {
            final String s = "Имейл должен содержать символ @";
            log.info("Вызвано исключение: " + s + " Пришло: " + newUser.getEmail());
            throw new ValidationException(s);
        }
        if (!isValidEmailAddress(newUser.getEmail())) {
            final String s = "Не корректно введен Имейл";
            log.info("Вызвано исключение: " + s + " Пришло: " + newUser.getEmail());
            throw new ValidationException(s);
        }
        if (findEmail(newUser)) {
            final String s = "Этот имейл уже используется";
            log.info("Вызвано исключение: " + s + " Пришло: " + newUser.getEmail());
            throw new DuplicatedDataException(s);
        }
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            final String s = "Дата рождения не может быть в будущем";
            log.info("Вызвано исключение: " + s + " Пришло: " + newUser.getBirthday());
            throw new ValidationException(s);
        }
    }

    private boolean isValidEmailAddress(final String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    private boolean findEmail(final User newUser) {
        return users.values()
                .stream()
                .map(User::getEmail)
                .filter(item -> item.equals(newUser.getEmail()))
                .findFirst()
                .isPresent();
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
