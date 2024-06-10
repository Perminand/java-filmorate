package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilmJoinGenre {
    private long id;
    private String name;
    private Long film_id;
}

