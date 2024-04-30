package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class FilmService implements IntService<Film> {
    private final LocalDate dateMark;
    private FilmStorage filmStorage;

    @Autowired
    public FilmService(@Value("${filmorate.date-mark}") LocalDate dateMark,
                       FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        this.dateMark = dateMark;
    }

    @Override
    public Collection<Film> findAll() {
        return filmStorage.getAll();
    }

    @Override
    public Optional<Film> findById(long id){
        return filmStorage.getById(id);
    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }
    @Override
    public Optional<Film> create(Film data) {
        validate(data);
        log.debug("Film создан" + data);
        return filmStorage.create(data);
    }

    @Override
    public Optional<Film> update(Film data) {
        if (data.getId() == null) {
            throw new ValidationException("ID не должен содержать NULL");
        }
        validate(data);
        log.debug("Film обновлен" + data);
        return filmStorage.update(data);
    }

    public Optional<Film> addLike(long filmId, long userId){
        return filmStorage.addLike(filmId,userId);
    }

    private void validate(final Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(dateMark)) {
            final String s = "Дата релиза — не раньше 28 декабря 1895 года";
            log.info("Вызвано исключение: " + s + " Получено: " + film.getReleaseDate());
            throw new ValidationException(s);
        }
    }

    public Optional<Film> deleteLike(long filmId, long userId) {
        return filmStorage.deleteLike(filmId,userId);
    }


}
