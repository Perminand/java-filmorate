package ru.yandex.practicum.filmorate.dao.db.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.Storage;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Primary
public class GenreDbStorage implements Storage<Genre> {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper mapper;
    private PreparedStatement stmt = null;

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query("SELECT * FROM genres", mapper);
    }

    @Override
    public Optional<Genre> getById(long id) {
        String query = "SELECT * FROM genres WHERE genre_id = ?";
        final List<Genre> genres = jdbcTemplate.query(query, mapper, id);
        if (genres.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(genres.getFirst());
    }

    @Override
    public Optional<Genre> create(Genre data) {
        String query = "INSERT INTO genres (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            stmt = connection.prepareStatement(query, new String[]{"genre_id"});
            stmt.setString(3, data.getName());
            return stmt;
        }, keyHolder);
        return getById(keyHolder.getKey().longValue());
    }

    @Override
    public Optional<Genre> update(Genre data) {
        String sqlQuery =
                "update genres SET name = ? WHERE genre_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                data.getName(),
                data.getId()
        );
        return getById(data.getId());
    }

    @Override
    public void delete() {
        jdbcTemplate.update("DELETE FROM genres");
    }

    @Override
    public void deleteById(long id) {
        String query = "DELETE FROM genres WHERE genre_id = ?";
        jdbcTemplate.update(query, id);
    }

}
