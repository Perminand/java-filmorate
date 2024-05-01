package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NullFoundIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements IntService<User> {
    private final UserStorage userStorage;
    private final ValidateUser validateUser;

    public UserService(UserStorage userStorage, ValidateUser validateUser) {
        this.userStorage = userStorage;
        this.validateUser = validateUser;
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.getAll();
    }

    @Override
    public Optional<User> findById(long id) {
        return userStorage.getById(id);
    }

    public Collection<User> getFriends(long id) {
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    @Override
    public Optional<User> create(User data) {
        validateUser.validateAll(data);
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
        validateUser.validateBirthday(data);
        log.debug("User c ID " + data.getId() + " обновлен");
        return userStorage.update(data);
    }

    public Optional<User> addFriend(long userId, long friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public Optional<User> deleteFriend(long userId, long friendId) {
        return userStorage.deleteFriend(userId, friendId);
    }

}
