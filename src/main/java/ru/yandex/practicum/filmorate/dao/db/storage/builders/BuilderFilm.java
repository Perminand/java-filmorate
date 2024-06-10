package ru.yandex.practicum.filmorate.dao.db.storage.builders;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmJoinGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    static FilmJoinGenre makeFilmJoinGenre(ResultSet rs, int rowNum) throws SQLException {
        FilmJoinGenre filmJoinGenre = new FilmJoinGenre(
                rs.getLong("film_id"),
                rs.getString("name"),
                rs.getLong("genre_id")
                );
        return filmJoinGenre;
    }


    static Like makeLike(ResultSet rs, int rowNum) throws SQLException {
        return new Like(
                rs.getLong("film_id"),
                rs.getLong("user_id"));
    }

    public List<Genre> getFilmGenres(long filmId) {
        String query = "SELECT * FROM genres g, FILM_GENRE fg where fg.GENRE = g.GENRE_ID AND fg.FILM_ID = ?";
        List<Genre> genreList = jdbcTemplate.query(query, BuilderFilm::makeGenre, filmId);
        return genreList;
    }

    public List<FilmJoinGenre> getFilmJoinGenre(List<Long> list) {
        String query = "SELECT * FROM FILM_GENRE fg " +
                "LEFT JOIN genres g ON fg.GENRE=g.genre_id " +
                "WHERE fg.FILM_ID IN (?)";
        String s = list.toString();
        List<FilmJoinGenre> genreList = jdbcTemplate.query(query, BuilderFilm::makeFilmJoinGenre, s);
        return genreList;
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

//    public List<Film> buildListFilm(List<Film> list) {
//        List<Long> longList = new ArrayList<>();
//        for (Film f : list) {
//            longList.add(f.getId());
//        }
//        List<FilmJoinGenre> filmJoinGenres = getFilmJoinGenre(longList);
//        for (FilmJoinGenre fjg : filmJoinGenres)
//            for (Film f : list) {
//                if (fjg.getFilm_id() == f.getId()) {
//                    if (f.getGenres() == null) {
//                        f.setGenres(new ArrayList<>());
//                    }
//                    List<Genre> genreList = f.getGenres();
//                    genreList.add(new Genre(fjg.getId(), fjg.getName()));
//                }
//            }
//        return list;
//    }
}
