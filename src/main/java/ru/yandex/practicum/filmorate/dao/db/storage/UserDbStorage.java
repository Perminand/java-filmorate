package ru.yandex.practicum.filmorate.dao.db.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper mapper;
    private PreparedStatement stmt = null;

    @Override
    public List<User> getAll() {
        List<User> list = jdbcTemplate.query("SELECT * FROM USERS", mapper);
        return list;
    }

    @Override
    public Optional<User> getById(long id) {
        String query = "SELECT * FROM USERS WHERE user_id = ?";
        final List<User> users = jdbcTemplate.query(query, mapper, id);
        if (users.size() == 0) {
            throw new EntityNotFoundException("user id = " + id);
        }
        User user = users.getFirst();
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> create(User data) {
        String query = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            stmt = connection.prepareStatement(query, new String[]{"user_id"});
            stmt.setString(1, data.getEmail());
            stmt.setString(2, data.getLogin());
            stmt.setString(3, data.getName());
            stmt.setDate(4, Date.valueOf(data.getBirthday()));
            return stmt;
        }, keyHolder);
        return getById(keyHolder.getKey().longValue());
    }

    @Override
    public Optional<User> update(User data) {
        String sqlQuery =
                "update users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                data.getEmail(),
                data.getLogin(),
                data.getName(),
                data.getBirthday(),
                data.getId()
        );
        return getById(data.getId());
    }

    @Override
    public List<User> getFriends(long id) {
        String query = "SELECT * " +
                "FROM users " +
                "WHERE user_id " +
                "IN (SELECT f.USER_SECOND_ID  " +
                "FROM USERS u " +
                "LEFT JOIN FRIENDSHIP f " +
                "ON u.user_id = f.user_first_id " +
                "WHERE u.user_id = ?)";
        List<User> users = jdbcTemplate.query(query, mapper, id);
        return users;
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        String query = "SELECT * FROM USERS " +
                "WHERE user_id IN (" +
                "SELECT USER_SECOND_ID  FROM FRIENDSHIP " +
                "WHERE USER_FIRST_ID =? " +
                "AND USER_SECOND_ID IN(SELECT user_second_id FROM friendship WHERE user_first_id = ?));";
        return jdbcTemplate.query(query, mapper, userId, otherId);
    }

    @Override
    public boolean findEmail(User newUser) {
            List<User> userList = jdbcTemplate.query("Select * from users WHERE email = ?", mapper, newUser.getEmail());
        return !userList.isEmpty();
    }

    @Override
    public Optional<User> addFriend(long userId, long friendId) {
        String query = "MERGE INTO friendship VALUES (?, ?)";
        jdbcTemplate.update(query, userId, friendId);
        log.debug("Друг добавлен для user: " + userId);
        return Optional.empty();
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        String query = "DELETE FROM FRIENDSHIP WHERE user_first_id = ? AND user_second_id = ?";
                jdbcTemplate.update(query, userId, friendId);
                return null;
    }

    @Override
    public void delete() {
        jdbcTemplate.update("DELETE FROM users");
    }

    @Override
    public void deleteById(long id) {
        String query = "delete from users WHERE user_id = ?";
        jdbcTemplate.update(query, id);
    }

}
