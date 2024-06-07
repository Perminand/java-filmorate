package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.dal.db.storage.ListGenreDbStorage;
import ru.yandex.practicum.filmorate.dal.db.storage.builders.BuilderFilm;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService implements IntefaceService<Film> {
    private static final LocalDate DATE_MARK = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final BuilderFilm builderFilm;
    private final GenreService genreService;
    private final ListGenreDbStorage listGenreDbStorage;
    private final MpaService mpaService;

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(long id) {
        Film film = filmStorage.getById(id).orElseThrow(() -> new EntityNotFoundException("Нет film с заданным ID"));
        return builderFilm.build(film);

    }

    public List<Film> getPopular(int count) {
        return filmStorage.getPopular(count)
                .stream().map(builderFilm::build)
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .toList();
    }

    public Film create(Film data) {
        validate(data);
        if (data.getMpa() != null) {
            try {
                mpaService.getById(data.getMpa().getId());
            } catch (EntityNotFoundException e) {
                throw new ValidationException("нет mpa с заданным id:" + data.getMpa().getId());
            }
        }
        if (data.getGenres() != null) {
            for (Genre g : data.getGenres()) {
                try {
                    genreService.getById(g.getId());
                } catch (EntityNotFoundException e) {
                    throw new ValidationException("нет genre с заданным id:" + g.getId());
                }
            }
        }
        Film film = filmStorage.create(data).get();
        if (data.getGenres() != null) {
            if (!data.getGenres().isEmpty()) {
                listGenreDbStorage.addGenreToListGenges(data, film.getId());
            }
        }

        log.debug("Film создан" + data);
        return getById(film.getId());
    }

    public Film update(Film data) {
        if (data.getId() == null) {
            throw new ValidationException("ID не должен содержать NULL");
        }
        filmStorage.getById(data.getId());
        validate(data);
        log.debug("Film обновлен" + data);
        return builderFilm.build(filmStorage.update(data).get());
    }

    @Override
    public void delete() {

    }

    @Override
    public void deleteById(long id) {
        filmStorage.deleteById(id);
    }

    public Film addLike(long filmId, long userId) {
        Film film = getById(filmId);
        userService.getById(userId);
        filmStorage.addLike(filmId, userId);
        return getById(filmId);
    }

    public Optional<Film> deleteLike(long filmId, long userId) {
        getById(filmId);
        userService.getById(userId);
        filmId = filmStorage.deleteLike(filmId, userId).get().getId();
        return Optional.of(getById(filmId));
    }

    private void validate(final Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(DATE_MARK)) {
            final String s = "Дата релиза — не раньше 28 декабря 1895 года";
            log.info("Вызвано исключение: " + s + " Получено: " + film.getReleaseDate());
            throw new ValidationException(s);
        }
    }
}
