package ru.yandex.practicum.filmorate.dao.memory;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
public class DataStorage<T> {
    protected long generateId = 0L;
    protected final Map<Long, T> storage = new HashMap<>();

    public List<T> getAll() {
        return storage.values().stream().toList();
    }

    public Optional<T> getById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public void findId(long key) throws EntityNotFoundException {
        if (!storage.containsKey(key)) {
            final String s = "Нет запрошенного ИД";
            log.info("Вызвано исключение: " + s + " Получено: " + key);
            throw new EntityNotFoundException(s);
        }
    }
}
