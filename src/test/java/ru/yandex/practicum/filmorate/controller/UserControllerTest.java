package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dao.db.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserDbStorage.class, UserRowMapper.class, Validation.class })
class UserControllerTest {

    static final UserController userController = new UserController();
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
}