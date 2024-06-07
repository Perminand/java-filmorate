package ru.yandex.practicum.filmorate.dal.db.storage.builders;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
@AllArgsConstructor
public class BuilderUser {
    JdbcTemplate jdbcTemplate;



    public Set<Long> getUserFriends(long id) {
        String query = "SELECT USER_SECOND_ID FROM friendship WHERE USER_FIRST_ID = ?";
        Set<Long> set  = new HashSet<>();
        for (Friend f : jdbcTemplate.query(query, BuilderUser::makeFriend, id)) {
            set.add(f.getFirstId());
        }
        return set;
    }

    static Friend makeFriend(ResultSet rs, int rowNum) throws SQLException {
        return new Friend(
                rs.getLong("USER_FIRST_ID"),
                rs.getLong("USER_SECOND_ID"));
    }

    public User build(User user) {
        long id = user.getId();
        user.setFriends(getUserFriends(id));
        return user;
    }
}
