package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NullFoundIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;


@Slf4j
@Component
public class InMemoryUserStorage extends DataStorage<User> implements UserStorage {

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
    public void deleteById(long id) {

    }

    @Override
    public Collection<User> getFriends(long id) {
        final User user = storage.get(id);
        if (user == null) {
            throw new NullFoundIdException("Нет user с ID: " + id);
        }
        Set<Long> setUser = storage.get(id).getFriends();
        if (setUser == null) {
            return new ArrayList<>();
        }
        List<User> userList = new ArrayList<>();
        for (Long s : setUser) {
            userList.add(storage.get(s));
        }
        return userList;
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
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

        findId(userId);
        findId(friendId);
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
    public Optional<User> deleteFriend(long userId, long friendId) {
        final User user = storage.get(userId);
        final User friendUser = storage.get(friendId);
        if (user == null) {
            throw new NullFoundIdException("Нет user с ID: " + userId);
        } else if (friendUser == null) {
            throw new NullFoundIdException("Нет friends с ID: " + friendId);
        }
        final Set<Long> setUser = user.getFriends();
        if (setUser != null) {
            setUser.remove(friendId);
        }
        final Set<Long> setFriends = friendUser.getFriends();
        if (setFriends != null) {
            setFriends.remove(userId);
        }
        return Optional.ofNullable(storage.get(userId));
    }


}
