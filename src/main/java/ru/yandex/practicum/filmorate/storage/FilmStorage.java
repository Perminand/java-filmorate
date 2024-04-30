package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage extends Storage<Film> {

    Collection<Film> getPopular(int count);

    Optional<Film> addLike(long filmId, long userId);

    Optional<Film> deleteLike(long filmId, long userId);


}
