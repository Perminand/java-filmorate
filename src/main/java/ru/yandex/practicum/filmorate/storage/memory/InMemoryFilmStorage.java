package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage extends DataStorage<Film> implements FilmStorage {

    @Override
    public Optional<Film> create(Film data) {
        final long key = ++generateId;
        data.setId(key);
        storage.put(data.getId(), data);
        return Optional.ofNullable(storage.get(key));
    }

    @Override
    public Optional<Film> update(Film data) {
        final long key = data.getId();
        findId(key);
        storage.replace(data.getId(), data);
        return Optional.ofNullable(storage.get(key));
    }

    @Override
    public void deleteById(long id) {
        storage.remove(id);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        ArrayList<Film> films = new ArrayList<>(storage.values()
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                .limit(count).toList());
        return films;
    }

    @Override
    public Optional<Film> addLike(long filmId, long userId) {
        findId(filmId);
        final Film film = storage.get(filmId);
        Set<Long> likes = film.getLikes();
        if (likes == null) {
            likes = new HashSet<>();
        }
        likes.add(userId);
        film.setLikes(likes);
        return Optional.ofNullable(film);
    }

    @Override
    public Optional<Film> deleteLike(long filmId, long userId) {
        storage.get(filmId).getLikes().remove(userId);
        return Optional.ofNullable(storage.get(filmId));
    }

}
