package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage extends Storage<Film> {

    List<Film> getPopular(int count);

    Optional<Film> addLike(long filmId, long userId);

    Optional<Film> deleteLike(long filmId, long userId);

}
