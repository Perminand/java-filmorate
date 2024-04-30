package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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
public class FilmController implements Controller<Film> {
    private FilmService filmService;

    public FilmController() {
    }

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    @GetMapping("/{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public Film getById(@PathVariable("filmId") long id) {
        return filmService.findById(id)
                .orElseThrow(() -> new NullPointerException("Нет film с заданным ID"));

    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getPopular(@RequestParam(required = false) Integer count) {
        if (count == null) {
            count = 10;
        }
        return filmService.getPopular(count);
    }


    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getAll() {
        return filmService.findAll();
    }


    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film data) {
        return filmService.create(data).get();
    }

    @Override
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film).get();
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film addLike(@PathVariable("filmId") long filmId,
                        @PathVariable("userId") long userId) {
        return filmService.addLike(filmId, userId).get();
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film deleteLike(@PathVariable("filmId") long filmId,
                           @PathVariable("userId") long userId) {
        return filmService.deleteLike(filmId, userId).get();
    }


}

