package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.db.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService implements IntefaceService<Genre> {
    private final GenreDbStorage genreDbStorage;

    @Override
    public Collection<Genre> getAll() {
        return genreDbStorage.getAll();
    }

    @Override
    public Genre getById(final long id) {
        return genreDbStorage.getById(id).orElseThrow(() -> new EntityNotFoundException("No genre id = " + id));
    }

    @Override
    public Genre create(final Genre data) {
        return genreDbStorage.create(data).get();
    }

    @Override
    public Genre update(final Genre data) {
        return genreDbStorage.update(data).get();
    }

    @Override
    public void delete() {
        genreDbStorage.delete();
    }

    @Override
    public void deleteById(final long id) {
        genreDbStorage.deleteById(id);
    }
}
