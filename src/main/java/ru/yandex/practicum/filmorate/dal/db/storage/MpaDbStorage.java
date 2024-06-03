package ru.yandex.practicum.filmorate.dal.db.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.Storage;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Primary
public class MpaDbStorage implements Storage<Mpa> {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mapper;
    PreparedStatement stmt = null;

    @Override
    public List<Mpa> getAll() {
        return jdbcTemplate.query("SELECT * FROM film_rating", mapper);
    }

    @Override
    public Optional<Mpa> getById(long id) {
        String query = "SELECT * FROM film_rating WHERE film_rating_id = ?";
        final List<Mpa> listMpa = jdbcTemplate.query(query, mapper, id);
        if (listMpa.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(listMpa.getFirst());
    }

    @Override
    public Optional<Mpa> create(Mpa data) {
        String query = "INSERT INTO film_rating (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            stmt = connection.prepareStatement(query, new String[]{"film_rating_id"});
            stmt.setString(3, data.getName());
            return stmt;
        }, keyHolder);
        return getById(keyHolder.getKey().longValue());
    }

    @Override
    public Optional<Mpa> update(Mpa data) {
        String sqlQuery =
                "update film_rating SET name = ? WHERE film_rating_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                data.getName(),
                data.getId()
        );
        return getById(data.getId());
    }

    @Override
    public void delete() {
        jdbcTemplate.update("DELETE FROM film_rating");
    }

    @Override
    public void deleteById(long id) {
        String query = "DELETE FROM film_rating WHERE film_rating_id = ?";
        jdbcTemplate.update(query, id);
    }

}
