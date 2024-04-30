package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface IntService<T> {
    public Collection<T> findAll();
    public Optional<T> findById(long id);
    public Optional<T> create (T data);
    public Optional<T> update(T data);

}
