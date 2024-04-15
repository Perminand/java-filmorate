package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private Validator validator;

    static final UserController userController = new UserController();

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    @Test
    void createUserOk() throws ValidationException {
        final User user = User.builder()
                .name("name")
                .login("login")
                .email("a@aa.ru")
                .birthday(LocalDate.now())
                .build();
        assertTrue(validator.validate(user).isEmpty());
    }

    @Test
    void createUserNullFiled() {
        final User user = null;
        assertThrows(NullPointerException.class,
                () -> userController.validate(user));
    }

    @Test
    void createUserEmailFiled() throws ValidationException {
        final User user = User.builder()
                .name("name")
                .login("login")
                .email("")
                .birthday(LocalDate.now())
                .build();
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void createUserNullEmailFiled() throws ValidationException {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> userController.create(User.builder()
                        .name("name")
                        .login("login")
                        .email(null)
                        .birthday(LocalDate.now())
                        .build()));
        assertEquals("email is marked non-null but is null", exception.getMessage());
    }

    @Test
    void createUserDoubleEmailFiled() {
        User user1 = User.builder()
                .name("name")
                .login("login")
                .email("a@aa")
                .birthday(LocalDate.now())
                .build();
        User user2 = User.builder()
                .name("name")
                .login("login")
                .email("a@aa")
                .birthday(LocalDate.now())
                .build();
        assertThrows(DuplicatedDataException.class, () -> {
            userController.create(user1);
            userController.create(user2);
        });
    }
    @Test
    void createUserBirthdayFiled() throws ValidationException {
        final User user = User.builder()
                .name("name")
                .login("login")
                .email("a@aa.ru")
                .birthday(LocalDate.now().plusYears(1))
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.validate(user));
        assertEquals("Дата рождения не может быть в будущем",
                exception.getMessage());
    }

    @Test
    void createUserNullNameOk() throws ValidationException, DuplicatedDataException {
        final User user = User.builder()
                .name(null)
                .login("login")
                .email("a@aa.ru")
                .birthday(LocalDate.now())
                .build();
        userController.validate(user);
    }
}