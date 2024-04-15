package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    private Validator validator;
    private final FilmController filmController = new FilmController();

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createFilmOk() throws ValidationException {
        final Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(1).build();
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    void createFilmNullFiled() {
        final Film film = null;
        assertThrows(NullPointerException.class,
                () -> filmController.validate(film));
    }

    @Test
    void createFilmNameNullFiled() {
        final Film film = Film.builder()
                .name(null)
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(1).build();
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void createFilmNameEmptyFiled() {
        final Film film = Film.builder()
                .name(null)
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(1).build();
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void createFilmDescriptionFiled() {
        final String s = "t".repeat(201); // Создаем строку 201 символ
        final Film film = Film.builder()
                .name("name")
                .description(s)
                .releaseDate(LocalDate.now())
                .duration(1).build();
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void createFilmReleaseDateFiled() {
        final Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1894, 1, 1))
                .duration(0).build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.create(film));
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года",
                exception.getMessage());
    }

    @Test
    void createFilmDurationFiled() {
        final Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1897,1,1))
                .duration(-1).build();
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void updateFilmValidId() {
        final Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1897, 1, 1))
                .duration(1).build();
        filmController.create(film);
        Film newFilm = film;
        newFilm.setName("newName");
        newFilm.setId(Long.valueOf("1"));
        filmController.update(newFilm);
        assertTrue(filmController.getAll().stream()
                .map(Film::getName)
                .anyMatch(item -> item.equals("newName")));
    }

    @Test
    void updateFilmNoValidId() {
        final Film film = Film.builder()
                .id(Long.valueOf("1"))
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1897, 1, 1))
                .duration(1).build();
        ValidationException exception = assertThrows(ValidationException.class, () ->
                filmController.update(film));
        assertEquals("Нет запрошенного ИД", exception.getMessage());

    }
}