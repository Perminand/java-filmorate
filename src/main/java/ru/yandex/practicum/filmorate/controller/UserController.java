package ru.yandex.practicum.filmorate.controller;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;
import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
@Setter
public class UserController {


    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping("/user")
    public User create(@RequestBody User user) throws ConditionsNotMetException, DuplicatedDataException {
        validate(user);
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User newUser = users.put(user.getId(), user);
        log.debug("User created: " + newUser);
        return newUser;
    }

    @PutMapping("/user")
    public User update(@RequestBody User user) throws ConditionsNotMetException, DuplicatedDataException {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        validate(user);
        User newUser = users.replace(user.getId(), user);
        log.debug("User update: " + newUser);
        return newUser;
    }

    public void validate(User newUser) throws ConditionsNotMetException, DuplicatedDataException {
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
        if(!isValidEmailAddress(newUser.getEmail())){
            String s = "Не корректно введен Имейл";
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
            throw new ConditionsNotMetException(s);
        }
    }
    private boolean isValidEmailAddress(String email){
        return EmailValidator.getInstance().isValid(email);
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
