package ru.yandex.practicum.filmorate.controller;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class FilmControllerTest {
    private static final int PORT = 8080;
    static FilmController filmController = new FilmController();

    @Test
    void createFilmOk() throws ConditionsNotMetException {
        final Film validFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(Duration.ZERO).build();
        filmController.validate(validFilm);
    }

    @Test
    void createFilmNameFiled() throws ConditionsNotMetException {
        Film validFilm = Film.builder()
                .name("")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(Duration.ZERO).build();
        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> filmController.validate(validFilm));
        assertEquals("Название не может быть пустым",
                exception.getMessage());
    }

    @Test
    void createFilmDescriptionFiled() throws ConditionsNotMetException {
        StringBuilder s = new StringBuilder(""); // Создаем строку больше 201 символа
        for (int i=0;i<201;i++){
            s.append("t"); // Рандомный символ
        }
        Film validFilm = Film.builder()
                .name("name")
                .description(new String(s))
                .releaseDate(LocalDate.now())
                .duration(Duration.ZERO).build();
        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> filmController.validate(validFilm));
        assertEquals("Максимальная длина описания — 200 символов",
                exception.getMessage());
    }

    @Test
    void createFilmReleaseDateFiled() throws ConditionsNotMetException {
        Film validFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1894,1,1))
                .duration(Duration.ZERO).build();
        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> filmController.validate(validFilm));
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года",
                exception.getMessage());
    }
    @Test
    void createFilmDurationFiled() throws ConditionsNotMetException {
        Film validFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1897,1,1))
                .duration(Duration.ofSeconds(-1)).build();
        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> filmController.validate(validFilm));
        assertEquals("Продолжительность фильма должна быть положительным числом",
                exception.getMessage());
    }
}