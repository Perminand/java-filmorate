package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll (){
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ConditionsNotMetException {
        validate(film);
        film.setId(getNextId());
        Film newFilm = films.put(film.getId(),film);
        log.debug("film create" + newFilm);
        return newFilm;
    }

    public Film update(@RequestBody Film film) throws ConditionsNotMetException {
        validate(film);
        Film newFilm = films.replace(film.getId(), film);
        log.debug("film update" + newFilm);
        return newFilm;
    }

    private void validate(Film film) throws ConditionsNotMetException {
        log.info("Запущена валидация");
        if (film.getName() == null || film.getName().isBlank()) {
            String s = "Название не может быть пустым";
            log.info("Вызвано исключение: " + s + " Получено: " + film.getName());
            throw new ConditionsNotMetException(s);
        }
        if (film.getDescription().length() > 200) {
            String s = "максимальная длина описания — 200 символов";
            log.info("Вызвано исключение: " + s + " Получено: " + film.getDescription().length() + " символов"
                    + film.getDescription().length());
            throw new ConditionsNotMetException(s);
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            String s = "Дата релиза — не раньше 28 декабря 1895 года";
            log.info("Вызвано исключение: " + s + " Получено: " + film.getReleaseDate());
            throw new ConditionsNotMetException(s);
        }
        if (film.getDuration().getSeconds() < 0) {
            String s = "Продолжительность фильма должна быть положительным числом.";
            log.info("Вызвано исключение: " + s + " Получено: " + film.getDuration().getSeconds());
                        throw new ConditionsNotMetException(s);
        }

    }
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
