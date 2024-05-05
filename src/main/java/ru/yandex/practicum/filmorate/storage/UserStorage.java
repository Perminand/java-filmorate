package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage extends Storage<User> {

    Collection<User> getFriends(long id);

    Collection<User> getCommonFriends(long userId, long otherId);

    boolean findEmail(final User newUser);

    Optional<User> addFriend(long userId, long friendId);

    User deleteFriend(long userId, long friendId);


}
