package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilmJoinGenre {
    private long film_id;
    private Long id;
    private String name;
}

