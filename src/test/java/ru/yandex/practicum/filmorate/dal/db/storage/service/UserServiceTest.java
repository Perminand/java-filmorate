package ru.yandex.practicum.filmorate.dal.db.storage.service;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.UserStorage;
import ru.yandex.practicum.filmorate.dal.memory.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {
    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(userStorage,null);
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        int i = 1;
        do {
            User user = User.builder()
                    .name("name" + i)
                    .login("login" + i)
                    .email("a@aa" + i + ".ru")
                    .birthday(LocalDate.now())
                    .build();
            userService.create(user);
            i++;
        } while (i != 10);
    }

    @Test
    void create() {
        int i = userService.getAll().size();
        User user = User.builder()
                .name("name10")
                .login("login10")
                .email("a@aa11.ru")
                .birthday(LocalDate.now())
                .build();
        userService.create(user);
        assertEquals(userService.getAll().size(), i + 1);
    }

    @Test
    void createDoubleEmail() {
        int i = userService.getAll().size();
        User user = User.builder()
                .name("name10")
                .login("login10")
                .email("a@aa10.ru")
                .birthday(LocalDate.now())
                .build();
        assertEquals(userService.create(user), user);
        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class, () ->
                userService.create(user));
    }

    @Test
    void update() {
        long i = 3;
        User user = User.builder()
                .id(i)
                .name("newName")
                .login("login" + i)
                .email("a@aa" + i + ".ru")
                .birthday(LocalDate.now())
                .build();
        userService.update(user);
        assertEquals(userService.getById(i).getName(), "newName");


    }

    @Test
    void findAll() {
        assertEquals(userService.getAll().size(), 9);
    }

    @Test
    void addFriendGetFrends() {
        int i = 2;
        do {
            userService.addFriend(1, i);
            i++;
        } while (i != 10);
        assertEquals(userService.getById(1).getFriends().size(), 8);
    }

    @Test
    void getCommonFriends() {
        userService.addFriend(1, 2);
        userService.addFriend(2, 3);
        assertEquals(userService.getCommonFriends(1, 3).size(), 1);

    }

    @Test
    void deleteFriend() {
        userService.addFriend(1, 2);
        userService.deleteFriend(1, 2);
        assertEquals(userService.getFriends(1).size(), 0);
    }
}