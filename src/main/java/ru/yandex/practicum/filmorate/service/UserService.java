package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserStorage;
import ru.yandex.practicum.filmorate.dal.db.storage.builders.BuilderUser;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IntefaceService<User> {
    private final UserStorage userStorage;
    private final BuilderUser builderUser;// = new BuilderUser();

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(long id) {
        User user = userStorage.getById(id).orElseThrow(() -> new EntityNotFoundException("Нет user с заданным ID"));
        return builderUser.build(user);
    }

    public List<User> getFriends(long id) {
        getById(id);
        List<User> userList = userStorage.getFriends(id)
                .stream()
                .map(builderUser::build)
                .collect(Collectors.toList());
        return userList;
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        return userStorage.getCommonFriends(userId, otherId)
                .stream()
                .map(builderUser::build)
                .collect(Collectors.toList());
    }

    public User create(User data) {
        validateAll(data);
        if (data.getName() == null || data.getName().isBlank()) {
            data.setName(data.getLogin());
        }
        log.debug("User создан: " + data);
        data = userStorage.create(data).get();
        return getById(data.getId());
    }

    public User update(User data) {
        Long id = data.getId();
        if (id == null) {
            throw new ValidationException("id должен быть указан");
        }
        User user = userStorage.getById(id).get();
        final Optional<User> userOptional = userStorage.getById(data.getId());
        userOptional.orElseThrow(() -> new EntityNotFoundException("Нет user c id:" + data.getId()));
        validateBirthday(data);
        log.debug("User c id: " + data.getId() + " обновлен");
        return userStorage.update(data).get();
    }

    @Override
    public void delete() {
        userStorage.delete();
    }

    @Override
    public void deleteById(long id) {
        userStorage.deleteById(id);
    }

    public void addFriend(long userId, long friendId) {
        userStorage.getById(userId);
        userStorage.getById(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public User deleteFriend(long userId, long friendId) {
        userStorage.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Нет user с ID: " + userId));
        userStorage.getById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Нет friends с ID: " + friendId));
        return builderUser.build(userStorage.deleteFriend(userId, friendId));
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
