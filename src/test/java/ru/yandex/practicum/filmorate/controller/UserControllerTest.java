package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private static final int PORT = 8080;
    static final UserController userController = new UserController();

    @Test
    void createUserOk() throws ValidationException, DuplicatedDataException {
        final User validUser = User.builder()
                .name("name")
                .login("login")
                .email("a@aa.ru")
                .birthday(LocalDate.now())
                .build();
        userController.validate(validUser);
    }

    @Test
    void createUserNullFiled() {
        final User validUser = null;
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> userController.validate(validUser));
    }

    @Test
    void createUserEmailFiled() throws ValidationException {
        final User validUser = User.builder()
                .name("name")
                .login("login")
                .email("")
                .birthday(LocalDate.now())
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.validate(validUser));
        assertEquals("Имейл должен быть указан",
                exception.getMessage());
        validUser.setEmail("asd");
        exception = assertThrows(ValidationException.class,
                () -> userController.validate(validUser));
        assertEquals("Имейл должен содержать символ @",
                exception.getMessage());
    }

    @Test
    void createUserDoubleEmailFiled() throws ValidationException {
        final User validUser = User.builder()
                .name("name")
                .login("login")
                .email("a@aau")
                .birthday(LocalDate.now())
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.validate(validUser));
        assertEquals("Не корректно введен Имейл",
                exception.getMessage());
    }

    @Test
    void createUserBirthdayFiled() throws ValidationException {
        final User validUser = User.builder()
                .name("name")
                .login("login")
                .email("a@aa.ru")
                .birthday(LocalDate.now().plusYears(1))
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.validate(validUser));
        assertEquals("Дата рождения не может быть в будущем",
                exception.getMessage());
    }

    @Test
    void createUserNullNameOk() throws ValidationException, DuplicatedDataException {
        final User validUser = User.builder()
                .name(null)
                .login("login")
                .email("a@aa.ru")
                .birthday(LocalDate.now())
                .build();
        userController.validate(validUser);
    }
}