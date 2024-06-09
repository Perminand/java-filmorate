package ru.yandex.practicum.filmorate.dao.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


@Slf4j
@Component
public class InMemoryUserStorage extends DataStorage<User> implements UserStorage {

    @Override
    public Optional<User> getById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<User> create(User data) {
        final long key = ++generateId;
        data.setId(key);
        storage.put(data.getId(), data);
        return Optional.ofNullable(storage.get(key));
    }

    @Override
    public Optional<User> update(final User data) throws ValidationException, DuplicatedDataException {
        final long key = data.getId();
        findId(key);
        storage.replace(data.getId(), data);
        return Optional.ofNullable(storage.get(key));
    }


    @Override
    public List<User> getFriends(long id) {
        final User user = storage.get(id);
        if (user == null) {
            throw new EntityNotFoundException("Нет user с ID: " + id);
        }
        Set<Long> setUser = storage.get(id).getFriends();
        if (setUser == null) {
            return new ArrayList<>();
        }
        List<User> userList = new ArrayList<>();
        for (long s : setUser) {
            userList.add(storage.get(s));
        }
        return userList;
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        final List<User> listUser = new ArrayList<>();
        storage.get(userId)
                .getFriends()
                .stream()
                .filter(l -> storage.get(otherId).getFriends().contains(l))
                .forEach(l -> listUser.add(storage.get(l)));
        return listUser;
    }

    public boolean findEmail(final User newUser) {
        return storage.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(item -> item.equals(newUser.getEmail()));
    }

    @Override
    public Optional<User> addFriend(long userId, long friendId) {
        final User user = storage.get(userId);
        Set<Long> userFriends = user.getFriends();
        if (userFriends == null) {
            userFriends = new HashSet<>();
        }
        userFriends.add(friendId);
        user.setFriends(userFriends);

        Set<Long> friendFriends = storage.get(friendId).getFriends();
        if (friendFriends == null) {
            friendFriends = new HashSet<>();
        }
        friendFriends.add(userId);
        storage.get(friendId).setFriends(friendFriends);

        return Optional.of(user);
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        final Set<Long> setUser = storage.get(userId).getFriends();
        if (setUser != null) {
            setUser.remove(friendId);
        }
        final Set<Long> setFriends = storage.get(friendId).getFriends();
        if (setFriends != null) {
            setFriends.remove(userId);
        }
        return storage.get(userId);
    }

    @Override
    public void delete() {
        storage.clear();
    }

    @Override
    public void deleteById(long id) {
        storage.remove(id);
    }

}
