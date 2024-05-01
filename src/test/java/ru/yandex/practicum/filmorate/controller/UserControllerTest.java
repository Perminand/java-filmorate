package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.ValidateUser;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    static final UserController userController = new UserController();

    UserStorage userStorage = new InMemoryUserStorage();
    UserService userService = new UserService(userStorage, new ValidateUser(userStorage));
    private Validator validator;

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
            userService.create(user1);
            userService.create(user2);
        });
    }

}