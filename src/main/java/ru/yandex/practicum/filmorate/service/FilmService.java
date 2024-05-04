package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class FilmService {
    private final static LocalDate DATE_MARK = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserService userService;


    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> findAll() {
        return filmStorage.getAll();
    }

    public Film getById(long id) {
        return filmStorage.getById(id).orElseThrow(() -> new NullPointerException("Нет film с заданным ID"));

    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }

    public Optional<Film> create(Film data) {
        validate(data);
        log.debug("Film создан" + data);
        return filmStorage.create(data);
    }

    public Optional<Film> update(Film data) {
        if (data.getId() == null) {
            throw new ValidationException("ID не должен содержать NULL");
        }
        filmStorage.findId(data.getId());
        validate(data);
        log.debug("Film обновлен" + data);
        return filmStorage.update(data);
    }

    public Film addLike(long filmId, long userId) {
        Film film = getById(filmId);
        userService.getById(userId);
        filmStorage.addLike(filmId, userId);
        return film;
    }

    public Optional<Film> deleteLike(long filmId, long userId) {
        getById(filmId);
        userService.getById(userId);
        return filmStorage.deleteLike(filmId, userId);
    }

    private void validate(final Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(DATE_MARK)) {
            final String s = "Дата релиза — не раньше 28 декабря 1895 года";
            log.info("Вызвано исключение: " + s + " Получено: " + film.getReleaseDate());
            throw new ValidationException(s);
        }
    }




}
