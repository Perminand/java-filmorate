package ru.yandex.practicum.filmorate.dal.db.storage.builders;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
public class BuilderUser {
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();



    public Set<Long> getUserFriends(long id) {
        String query = "SELECT * FROM FRIENDSHIP WHERE user_first_id = ?";
        Set<Long> set  = new HashSet<>();
         List<Friend> friendList = jdbcTemplate.query(query, BuilderUser::makeFriend, id);
//        for (Friend f : )) {
//            set.add(f.getSecondId());
//        }
        return set;
    }

    static Friend makeFriend(ResultSet rs, int rowNum) throws SQLException {
        return new Friend(
                rs.getLong("user_first_id"),
                rs.getLong("user_second_id"));
    }

    public User build(User user) {
        long id = user.getId();
        user.setFriends(getUserFriends(id));
        return user;
    }
}
