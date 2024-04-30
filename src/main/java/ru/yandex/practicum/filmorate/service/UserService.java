package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NullFoundIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements IntService<User> {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.getAll();
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.empty();
    }

    public Collection<User> getFriends(long id) {
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    @Override
    public Optional<User> create(User data) {
        validate(data);
        if (data.getName() == null || data.getName().isBlank()) {
            data.setName(data.getLogin());
        }
        log.debug("User создан: " + data);
        return userStorage.create(data);
    }

    @Override
    public Optional<User> update(User data) {
        if (data.getId() == null) {
            throw new ValidationException("ID должен быть указан");
        }
        final Optional<User> userOptional = userStorage.getById(data.getId());
        userOptional.orElseThrow(() -> new NullFoundIdException("Нет user c ID:" + data.getId()));
        validate(data);
        log.debug("User c ID " + data.getId() + " обновлен");
        return userStorage.update(data);
    }

    public void validate(final User newUser) {
        if (userStorage.findEmail(newUser)) {
            final String s = "Этот имейл уже используется";
            log.info("Вызвано исключение: " + s + " Пришло: " + newUser.getEmail());
            throw new DuplicatedDataException(s);
        }
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            final String s = "Дата рождения не может быть в будущем";
            log.info("Вызвано исключение: " + s + " Пришло: " + newUser.getBirthday());
            throw new ValidationException(s);
        }
    }


    public Optional<User> addFriend(long userId, long friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public Optional<User> deleteFriend(long userId, long friendId) {
        return userStorage.deleteFriend(userId, friendId);
    }

}
