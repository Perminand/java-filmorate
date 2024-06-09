package ru.yandex.practicum.filmorate.dao.db.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

@RequiredArgsConstructor
@Repository
public class ListGenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addGenreToListGenges(Film data, long id) {
        String query = "MERGE INTO FILM_GENRE " +
                "(FILM_ID, GENRE) " +
                "VALUES (?, ?)";
        for (Genre g : data.getGenres()) {
            jdbcTemplate.update(query, id, g.getId());
        }

    }
}
