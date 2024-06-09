package ru.yandex.practicum.filmorate.dao.db.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper mapper;
    private PreparedStatement stmt = null;

    @Override
    public List<Film> getPopular(int count) {
        String query = "SELECT f.FILM_ID, " +
                "f.NAME, f.DESCRIPTION, " +
                "f.RELEASE_DATE, " +
                "f.DURATION, " +
                "fr.film_rating_id, " +
                "fr.NAME AS name_mpa, " +
                "fr.DESCRIPTION AS description_mpa " +
                "FROM films f " +
                "LEFT JOIN film_rating fr ON f.film_rating=fr.film_rating_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(f.FILM_ID) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(query, mapper, count);
    }

    @Override
    public Optional<Film> addLike(long filmId, long userId) {
        String query2 = "MERGE INTO likes VALUES (?, ?)";
        jdbcTemplate.update(query2, filmId, userId);
        return Optional.empty();
    }

    @Override
    public Optional<Film> deleteLike(long filmId, long userId) {
        String query = "delete from likes WHERE film_id = ? and user_id = ?";
        jdbcTemplate.update(query, filmId, userId);
        return getById(filmId);
    }

    @Override
    public List<Film> getAll() {
        return jdbcTemplate.query("SELECT f.FILM_ID, " +
                "f.NAME, f.DESCRIPTION, " +
                "f.RELEASE_DATE, " +
                "f.DURATION, " +
                "fr.film_rating_id, " +
                "fr.NAME AS name_mpa, " +
                "fr.DESCRIPTION AS description_mpa " +
                "FROM films f " +
                "LEFT JOIN film_rating fr ON f.film_rating=fr.film_rating_id;", mapper);
    }

    @Override
    public Optional<Film> getById(long id) {
        String query = "SELECT f.FILM_ID, " +
                "f.NAME, f.DESCRIPTION, " +
                "f.RELEASE_DATE, " +
                "f.DURATION, fr.film_rating_id, " +
                "fr.NAME AS name_mpa, " +
                "fr.DESCRIPTION AS description_mpa " +
                "FROM films f " +
                "LEFT JOIN film_rating fr ON f.film_rating=fr.film_rating_id " +
                "LEFT JOIN likes l ON f.FILM_ID=l.FILM_ID " +
                "WHERE f.film_id = ?";
        final List<Film> films = jdbcTemplate.query(query, mapper, id);
        if (films.size() != 1) {
            throw new EntityNotFoundException("user id = " + id);
        }
        return Optional.of(films.getFirst());
    }

    @Override
    public Optional<Film> create(Film data) {
        String query = "INSERT INTO films (name, description, release_date, duration, film_rating) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            stmt = connection.prepareStatement(query, new String[]{"film_id"});
            stmt.setString(1, data.getName());
            stmt.setString(2, data.getDescription());
            stmt.setDate(3, Date.valueOf(data.getReleaseDate()));
            stmt.setInt(4, data.getDuration());
            stmt.setLong(5, data.getMpa().getId());
            return stmt;
        }, keyHolder);
        return getById(keyHolder.getKey().longValue());
    }

    @Override
    public Optional<Film> update(Film data) {
        String sqlQuery =
                "update films SET name = ?, description = ?, release_date = ?, duration = ? WHERE Film_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                data.getName(),
                data.getDescription(),
                data.getReleaseDate(),
                data.getDuration(),
                data.getId()
        );
        return getById(data.getId());
    }

    @Override
    public void delete() {
        jdbcTemplate.update("DELETE FROM films");
    }

    @Override
    public void deleteById(long id) {
        String query = "DELETE FROM films WHERE films_id = ?";
        jdbcTemplate.update(query, id);
    }
}
