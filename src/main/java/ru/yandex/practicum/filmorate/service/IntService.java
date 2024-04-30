package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.Optional;

public interface IntService<T> {
    Collection<T> findAll();

    Optional<T> findById(long id);

    Optional<T> create(T data);

    Optional<T> update(T data);

}
