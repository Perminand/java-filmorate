//package ru.yandex.practicum.filmorate.controller;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
//import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
//public abstract class Controller<T> {
//    private final Map<Long, T> map = new HashMap<>();
//
//    public Collection<T> getAll() {
//        return map.values();
//    }
//
//    public User create(T t) throws ConditionsNotMetException, DuplicatedDataException {
//        T tn = t.
//        User newUser = map.put(t.getId(), t);
//        log.debug("User created: " + newUser);
//        return newUser;
//    }
//
//    private long getNextId() {
//        long currentMaxId = map.keySet()
//                .stream()
//                .mapToLong(id -> id)
//                .max()
//                .orElse(0);
//        return ++currentMaxId;
//    }
//}
