package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage extends Storage<User> {

    List<User> getFriends(long id);

    List<User> getCommonFriends(long userId, long otherId);

    boolean findEmail(final User newUser);

    Optional<User> addFriend(long userId, long friendId);

    User deleteFriend(long userId, long friendId);


}
