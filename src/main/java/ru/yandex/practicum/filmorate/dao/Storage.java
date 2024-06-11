package ru.yandex.practicum.filmorate.dao;

import java.util.List;
import java.util.Optional;

public interface Storage<T> {
    List<T> getAll();

    Optional<T> getById(long id);

    Optional<T> create(T data);

    Optional<T> update(T data);

    void delete();

    void deleteById(long id);


}
