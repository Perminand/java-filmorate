package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll (){
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ConditionsNotMetException {
        if(film.getName()==null||film.getName().isBlank()){
            throw new ConditionsNotMetException("название не может быть пустым");
        }
        if(film.getDescription().length()>200){
            throw new ConditionsNotMetException("максимальная длина описания — 200 символов");
        }
        if(film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))){
            throw new ConditionsNotMetException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration().getSeconds()<0){
            throw new ConditionsNotMetException("продолжительность фильма должна быть положительным числом.");
        }
        film.setId(getNextId());
        return films.put(film.getId(),film);
    }

    public Film update(@RequestBody Film film) throws ConditionsNotMetException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ConditionsNotMetException("название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ConditionsNotMetException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ConditionsNotMetException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration().getSeconds() < 0) {
            throw new ConditionsNotMetException("продолжительность фильма должна быть положительным числом.");
        }
        return films.replace(film.getId(), film);
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
