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
    private static final int MAX_lENGTH_DESCRIPTION = 200;
    private static final LocalDate DATE_MARK = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ConditionsNotMetException {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("film create" + film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ConditionsNotMetException {
        validate(film);
        films.replace(film.getId(), film);
        log.debug("film update" + film);
        return film;
    }

    public void validate(final Film film) throws ConditionsNotMetException {
        if (film.getName() == null || film.getName().isBlank()) {
            final String s = "Название не может быть пустым";
            log.info("Вызвано исключение: " + s + " Получено: " + film.getName());
            throw new ConditionsNotMetException(s);
        }
        if (film.getDescription().length() > MAX_lENGTH_DESCRIPTION) {
            final String s = "Максимальная длина описания — " + MAX_lENGTH_DESCRIPTION + " символов";
            log.info("Вызвано исключение: " + s + " Получено: " + film.getDescription().length() + " символов"
                    );
            throw new ConditionsNotMetException(s);
        }
        if (film.getReleaseDate().isBefore(DATE_MARK)) {
            final String s = "Дата релиза — не раньше 28 декабря 1895 года";
            log.info("Вызвано исключение: " + s + " Получено: " + film.getReleaseDate());
            throw new ConditionsNotMetException(s);
        }
        if (film.getDuration() < 0) {
            final String s = "Продолжительность фильма должна быть положительным числом";
            log.info("Вызвано исключение: " + s + " Получено: " + film.getDuration());
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
