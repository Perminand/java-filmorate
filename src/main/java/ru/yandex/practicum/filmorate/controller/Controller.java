package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;

public interface Controller<T> {

    Collection<T> getAll();

    T getById(long id);

    T create(final T data);

    T update(T data);

}
