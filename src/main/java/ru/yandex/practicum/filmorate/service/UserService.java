package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.getAll();
    }

    public User getById(long id) {

        return userStorage.getById(id).orElseThrow(() -> new NullPointerException("Нет user с заданным ID"));

    }

    public Collection<User> getFriends(long id) {
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    public Optional<User> create(User data) {
        validateAll(data);
        if (data.getName() == null || data.getName().isBlank()) {
            data.setName(data.getLogin());
        }
        log.debug("User создан: " + data);
        return userStorage.create(data);
    }

    public Optional<User> update(User data) {
        if (data.getId() == null) {
            throw new ValidationException("ID должен быть указан");
        }
        final Optional<User> userOptional = userStorage.getById(data.getId());
        userOptional.orElseThrow(() -> new EntityNotFoundException("Нет user c ID:" + data.getId()));
        validateBirthday(data);
        log.debug("User c ID " + data.getId() + " обновлен");
        return userStorage.update(data);
    }

    public Optional<User> addFriend(long userId, long friendId) {
        userStorage.findId(userId);
        userStorage.findId(friendId);
        return userStorage.addFriend(userId, friendId);
    }

    public User deleteFriend(long userId, long friendId) {
        final User user = userStorage.getById(userId).get();
        final User friendUser = userStorage.getById(userId).get();
        if (user == null) {
            throw new EntityNotFoundException("Нет user с ID: " + userId);
        } else if (friendUser == null) {
            throw new EntityNotFoundException("Нет friends с ID: " + friendId);
        } else {
            userStorage.deleteFriend(userId, friendId);
        }


        return userStorage.deleteFriend(userId, friendId);
    }

    public void validateAll(final User newUser) {
        validateEmail(newUser);
        validateBirthday(newUser);
    }

    public void validateEmail(final User newUser) {
        if (userStorage.findEmail(newUser)) {
            final String s = "Этот имейл уже используется";
            log.info("Вызвано исключение: " + s + " Пришло: " + newUser.getEmail());
            throw new DuplicatedDataException(s);
        }
    }

    public void validateBirthday(final User newUser) {
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            final String s = "Дата рождения не может быть в будущем";
            log.info("Вызвано исключение: " + s + " Пришло: " + newUser.getBirthday());
            throw new ValidationException(s);
        }
    }

}
