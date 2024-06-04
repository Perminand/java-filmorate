package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.valves.JDBCAccessLogValve;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.db.storage.*;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.dal.memory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.dal.memory.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserDbStorage.class, UserRowMapper.class, })
class FilmControllerTest {
    JdbcTemplate jdbcTemplate = new JdbcTemplate();
    private FilmService filmService = new FilmService(
            new FilmDbStorage(jdbcTemplate,new FilmRowMapper()),
            new UserService(new UserDbStorage(jdbcTemplate,new UserRowMapper())),
            new GenreService(new GenreDbStorage(jdbcTemplate,new GenreRowMapper())),
            new ListGenreDbStorage(jdbcTemplate),
            new MpaService(new MpaDbStorage(jdbcTemplate,new MpaRowMapper()))
    );
    private final FilmController filmController = new FilmController(filmService);

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
    }

    @Test
    void createFilmNullFiled() {
        final Film film = null;
        assertThrows(NullPointerException.class,
                () -> filmController.create(film));
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
                .releaseDate(LocalDate.of(1897, 1, 1))
                .duration(-1).build();
        assertFalse(validator.validate(film).isEmpty());
    }
}