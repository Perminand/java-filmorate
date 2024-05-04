package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    private final FilmService filmService = new FilmService(new InMemoryFilmStorage(), new UserService(new InMemoryUserStorage()));
    private Validator validator;

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
        assertEquals(filmService.create(film).get(), film);
    }

    @Test
    void createFilmNullFiled() {
        final Film film = null;
        assertThrows(NullPointerException.class,
                () -> filmService.create(film));
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
                () -> filmService.create(film));
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года",
                exception.getMessage());
    }

    @Test
    void createFilmDurationFiled() {
        final Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1897, 1, 1))
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
        filmService.create(film);
        Film newFilm = film;
        newFilm.setName("newName");
        newFilm.setId(Long.valueOf("1"));
        filmService.update(newFilm);
        assertTrue(filmService.findAll().stream()
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
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                filmService.update(film));
        assertEquals("Нет запрошенного ИД", exception.getMessage());

    }

}