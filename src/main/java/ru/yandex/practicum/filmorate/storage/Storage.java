package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Optional;

public interface Storage<T> {
    Collection<T> getAll();

    Optional<T> getById(long id);

    Optional<T> create(T data);

    Optional<T> update(T data);

    void deleteById(long id);


}
