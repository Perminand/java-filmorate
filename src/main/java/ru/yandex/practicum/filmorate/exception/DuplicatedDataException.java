package ru.yandex.practicum.filmorate.exception;

public class DuplicatedDataException extends RuntimeException {
    public DuplicatedDataException(String s) {
        super(s);
    }
}
