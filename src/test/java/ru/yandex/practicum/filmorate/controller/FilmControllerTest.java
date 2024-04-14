package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class FilmControllerTest {
    private static final int PORT = 8080;
    static final FilmController filmController = new FilmController();

    @Test
    void createFilmOk() throws ConditionsNotMetException {
        final Film validFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(0).build();
        filmController.validate(validFilm);
    }

    @Test
    void createFilmNullFiled() {
        final Film validFilm = null;
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> filmController.validate(validFilm));
    }

    @Test
    void createFilmNameFiled() {
        final Film validFilm = Film.builder()
                .name("")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(0).build();
        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> filmController.validate(validFilm));
        assertEquals("Название не может быть пустым",
                exception.getMessage());
    }

    @Test
    void createFilmDescriptionFiled() {
        final StringBuilder s = new StringBuilder(); // Создаем строку больше 201 символа
        for (int i = 0;i < 201; i++){
            s.append("t"); // Рандомный символ
        }
        final Film validFilm = Film.builder()
                .name("name")
                .description(new String(s))
                .releaseDate(LocalDate.now())
                .duration(0).build();
        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> filmController.validate(validFilm));
        assertEquals("Максимальная длина описания — 200 символов",
                exception.getMessage());
    }

    @Test
    void createFilmReleaseDateFiled() {
        final Film validFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1894,1,1))
                .duration(0).build();
        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> filmController.validate(validFilm));
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года",
                exception.getMessage());
    }

    @Test
    void createFilmDurationFiled() {
        final Film validFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1897,1,1))
                .duration(-1).build();
        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> filmController.validate(validFilm));
        assertEquals("Продолжительность фильма должна быть положительным числом",
                exception.getMessage());
    }
}