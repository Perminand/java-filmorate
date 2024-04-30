package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NullFoundIdException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
public class DataStorage<T> {
    protected long generateId = 0L;
    protected Map<Long, T> storage = new HashMap<>();

    public Collection<T> getAll() {
        return storage.values();
    }

    public Optional<T> getById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public void findId(long key) throws NullFoundIdException {
        if (!storage.containsKey(key)) {
            final String s = "Нет запрошенного ИД";
            log.info("Вызвано исключение: " + s + " Получено: " + key);
            throw new NullFoundIdException(s);
        }
    }
}
