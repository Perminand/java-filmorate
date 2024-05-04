package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;


@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public Film getById(@PathVariable("filmId") long id) {
        return filmService.getById(id);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopular(count);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getAll() {
        return filmService.findAll();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film data) {
        return filmService.create(data).get();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film).get();
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film addLike(@PathVariable("filmId") long filmId,
                        @PathVariable("userId") long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film deleteLike(@PathVariable("filmId") long filmId,
                           @PathVariable("userId") long userId) {
        return filmService.deleteLike(filmId, userId).get();
    }


}

