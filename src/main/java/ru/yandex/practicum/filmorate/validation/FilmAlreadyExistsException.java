package ru.yandex.practicum.filmorate.validation;

public class FilmAlreadyExistsException extends RuntimeException {
    public FilmAlreadyExistsException(String message) {
        super(message);
    }
}
