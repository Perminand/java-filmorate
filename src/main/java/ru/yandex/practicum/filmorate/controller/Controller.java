package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

public interface Controller<T> {


    Collection<T> getAll();

    T getById(long id);


    T create(final T data);

    T update(T data);


}
