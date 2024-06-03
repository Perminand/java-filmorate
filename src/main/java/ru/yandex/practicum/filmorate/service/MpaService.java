package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.db.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService implements IntefaceService<Mpa> {
    private final MpaDbStorage mpaDbStorage;

    @Override
    public Collection<Mpa> getAll() {
        return mpaDbStorage.getAll();
    }

    @Override
    public Mpa getById(final long id) {
        return mpaDbStorage.getById(id).orElseThrow(() -> new EntityNotFoundException("No mpa id = " + id));
    }

    @Override
    public Mpa create(final Mpa data) {
        return mpaDbStorage.create(data).get();
    }

    @Override
    public Mpa update(final Mpa data) {
        return mpaDbStorage.update(data).get();
    }

    @Override
    public void delete() {
        mpaDbStorage.delete();
    }

    @Override
    public void deleteById(final long id) {
        mpaDbStorage.deleteById(id);
    }
}
