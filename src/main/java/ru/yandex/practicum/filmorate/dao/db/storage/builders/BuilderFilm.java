package ru.yandex.practicum.filmorate.dao.db.storage.builders;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
@AllArgsConstructor
public class BuilderFilm {
    private final JdbcTemplate jdbcTemplate;

    static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getLong("genre_id"),
                rs.getString("name"));
    }


    static Like makeLike(ResultSet rs, int rowNum) throws SQLException {
        return new Like(
                rs.getLong("film_id"),
                rs.getLong("user_id"));
    }

    public List<Genre> getFilmGenres(long filmId) {
        String query = "SELECT * FROM genres g, FILM_GENRE fg where fg.GENRE = g.GENRE_ID AND fg.FILM_ID = ?";
        return jdbcTemplate.query(query, BuilderFilm::makeGenre, filmId);
    }


    public Set<Long> getFilmLike(long filmId) {
        String query = "SELECT * FROM likes WHERE film_id = ?";
        Set<Long> set = new HashSet<>();
        for (Like l : jdbcTemplate.query(query, BuilderFilm::makeLike, filmId)) {
            set.add(l.getUserId());
        }
        return set;
    }

    public Film build(Film film) {
        long id = film.getId();
        film.setGenres(getFilmGenres(id));
        film.setLikes(getFilmLike(id));
        return film;
    }
}
