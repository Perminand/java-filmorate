package ru.yandex.practicum.filmorate.dal.db.storage.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collections;
import java.util.List;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    private final UserService userService;
    private final FilmService filmService;
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void CreateUser() {
        User user = User.builder()
                .login("Mango11")
                .name("Melissa")
                .email("voiceemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        user = userService.create(user);

        assertEquals("voiceemail@mail.ru", userService.getById(user.getId()).getEmail());
    }

    @Test
    public void shouldUpdateUser() {
        User user = User.builder()
                .login("Mango11")
                .name("Melissa")
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        user = userService.create(user);

        User userUpdate = User.builder()
                .id(user.getId())
                .login("Mango22")
                .name("Anna")
                .email("palmtree@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        userUpdate = userService.update(userUpdate);

        assertEquals(userUpdate, userService.getById(user.getId()));
    }

    @Test
    public void shouldCreateUserWithEmptyName() {
        User user = User.builder()
                .login("Mangosteen11")
                .email("nicestmail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        user = userService.create(user);

        assertEquals("Mangosteen11", user.getName());
    }

    @Test
    void shouldNotPassEmailValidation() {
        User user = User.builder()
                .login("Mango11")
                .name("Melissa")
                .email("nicemailmail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotPassLoginValidationWithEmptyLogin() {
        User user = User.builder()
                .login("")
                .name("Melissa")
                .email("Jolly@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotPassLoginValidationWithBlanksInLogin() {
        User user = User.builder()
                .login(" Mango 11")
                .name("Melissa")
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotPassBirthdayValidation() {
        User user = User.builder()
                .login("Mango11")
                .name("Melissa")
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(3000, 8, 15))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test

    public void shouldAddFriend() {
        User user = User.builder()
                .login("Tango11")
                .name("Melissa")
                .email("rhino@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        user = userService.create(user);
        User friend = User.builder()
                .login("Sango11")
                .name("Melissa")
                .email("mouse@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        friend = userService.create(friend);

        userService.addFriend(user.getId(), friend.getId());

        assertEquals(List.of(friend), userService.getFriends(user.getId()));
    }

    @Test
    public void shouldDeleteFriend() {
        User user = User.builder()
                .login("Wind")
                .name("Melissa")
                .email("cheetah@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        user = userService.create(user);
        User friend = User.builder()
                .login("Iris")
                .name("Melissa")
                .email("cat@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        friend = userService.create(friend);

        userService.addFriend(user.getId(), friend.getId());
        userService.deleteFriend(user.getId(), friend.getId());
        user = userService.getById(user.getId());
        assertEquals(Collections.emptyList(), List.copyOf(user.getFriends()));
    }

    @Test
    public void shouldFindMutualFriend() {
        User user = User.builder()
                .login("Windy")
                .name("Melissa")
                .email("cheetahcat@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);
        User friend = User.builder()
                .login("Iris")
                .name("Melissa")
                .email("cattycat@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(friend);
        User mutualFriend = User.builder()
                .login("Rose")
                .name("Melissa")
                .email("rose@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(mutualFriend);

        userService.addFriend(user.getId(), mutualFriend.getId());
        userService.addFriend(friend.getId(), mutualFriend.getId());

        List<User> mutual = userService.getCommonFriends(user.getId(), friend.getId());

        assertEquals(List.of(mutualFriend), mutual);
    }

    @Test
    public void shouldReturnAllFriends() {
        User user = User.builder()
                .login("Windy")
                .name("Melissa")
                .email("snowy@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);
        User friend = User.builder()
                .login("Iris")
                .name("Melissa")
                .email("stormy@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        userService.create(friend);
        userService.addFriend(user.getId(), friend.getId());

        assertEquals(1, userService.getFriends(user.getId()).size());
    }

    @Test
    public void shouldDeleteUser() {
        User user = User.builder()
                .login("Hazel")
                .name("Melissa")
                .email("hazelnut@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        userService.create(user);
        userService.deleteById(user.getId());

        assertFalse(userService.getAll().contains(user));
    }

    @Test
    public void shouldCreateUserEvents() {
        User user = User.builder()
                .login("Iri")
                .name("Mel")
                .email("Meliry@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);
        User user2 = User.builder()
                .login("Markich")
                .name("Mark")
                .email("mark@mail.ru")
                .birthday(LocalDate.of(2002, 8, 15))
                .build();
        userService.create(user2);
        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .duration(192)
                .releaseDate(LocalDate.of(1981, 12, 6))
                .mpa(new Mpa(1L, null, null))
                .build();
        filmService.create(film);
    }
}