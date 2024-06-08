package ru.yandex.practicum.filmorate.service;

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
import ru.yandex.practicum.filmorate.dal.db.storage.*;
import ru.yandex.practicum.filmorate.dal.db.storage.builders.BuilderFilm;
import ru.yandex.practicum.filmorate.dal.db.storage.builders.BuilderUser;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {FilmService.class,
        FilmDbStorage.class,
        FilmRowMapper.class,
        UserService.class,
        UserDbStorage.class,
        UserRowMapper.class,
        BuilderUser.class,
        BuilderFilm.class,
        GenreService.class,
        GenreDbStorage.class,
        GenreRowMapper.class,
        ListGenreDbStorage.class,
        MpaService.class,
        MpaRowMapper.class,
        MpaDbStorage.class})
class FilmServiceTest {
    private final FilmService filmService;// = new FilmService(new InMemoryFilmStorage(), new UserService(new InMemoryUserStorage()));
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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
}