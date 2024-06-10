package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilmJoinGenre {
    long id;
    String name;
    private Long film_id;
}

