package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;

public interface IntefaceController<T> {
    Collection<T> getAll();

    T getById(Long id);

    T create(T data);

    T update(T data);

    void delete();

    void deleteById(Long id);
}
