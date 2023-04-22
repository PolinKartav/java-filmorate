package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest {
    FilmController controller = new FilmController();

    @Test
    void createFilmWithIncorrectName() {
        Film film = new Film(3, "", "It is too boring.", LocalDate.parse("1999-10-12"), 180);
        final ValidationException exception = assertThrows(ValidationException.class, () -> controller.createFilm(film));
        assertEquals("Нет названия фильма.", exception.getReason());
    }

    @Test
    void createFilmWithCorrectName() {
        Film film = new Film(3, "Avatar", "It is too boring.", LocalDate.parse("1999-10-12"), 180);
        assertEquals(film, controller.createFilm(film));
    }

    @Test
    void createFilmWithTooLongDescription() {
        Film film = new Film(3, "Avatar", "It is too boring.It is too boring.It is too boring." +
                "It is too boring.It is too boring.It is too boring.It is too boring.It is too boring.It is too boring.It is too boring.It is too boring." +
                "It is too boring.It is too boring.It is too boring.It is too boring.It is too boring.It is too boring.It is too boring.", LocalDate.parse("1999-10-12"), 180);
        final ValidationException exception = assertThrows(ValidationException.class, () -> controller.createFilm(film));
        assertEquals("Описание фильма не дожно превышать 200 символов.", exception.getReason());
    }

    @Test
    void createFilmWithCorrectDescription() {
        Film film = new Film(1, "Avatar", "It is too boring.", LocalDate.parse("1899-12-28"), 180);
        assertEquals(film, controller.createFilm(film));
    }

    @Test
    void createFilmWithIncorrectRelease() {
        Film film = new Film(3, "Avatar", "It is too boring.", LocalDate.parse("1895-12-27"), 180);
        final ValidationException exception = assertThrows(ValidationException.class, () -> controller.createFilm(film));
        assertEquals("Релиз фильма не может быть опубликован раньше 28.12.1895.", exception.getReason());
    }

    @Test
    void createFilmWithCorrectRelease() {
        Film film = new Film(1, "Avatar", "It is too boring.", LocalDate.parse("1895-12-28"), 180);
        assertEquals(film, controller.createFilm(film));
    }

    @Test
    void createFilmWithInCorrectDuration() {
        Film film = new Film(1, "Avatar", "It is too boring.", LocalDate.parse("1895-12-28"), -1);
        final ValidationException exception = assertThrows(ValidationException.class, () -> controller.createFilm(film));
        assertEquals("Продолжительность фильма не может быть 0.", exception.getReason());
    }

    @Test
    void createFilmWithCorrectDuration() {
        Film film = new Film(1, "Avatar", "It is too boring.", LocalDate.parse("1895-12-28"), 1);
        assertEquals(film, controller.createFilm(film));
    }

    @Test
    void updateFilm() {
        Film film = new Film(1, "Avatar", "It is too boring.", LocalDate.parse("1895-12-28"), 180);
        controller.createFilm(film);
        Film film2 = new Film(1, "Avatar", "It is one of the greatest film ever.", LocalDate.parse("1895-12-28"), 1);
        assertEquals(film2, controller.updateFilm(film2));
    }

    @Test
    void updateFilmWithIncorrectId() {
        Film film = new Film(1, "Avatar", "It is too boring.", LocalDate.parse("1895-12-28"), 180);
        controller.createFilm(film);
        Film film2 = new Film(2, "Avatar", "It is one of the greatest film ever.", LocalDate.parse("1895-12-28"), 1);
        final ValidationException exception = assertThrows(ValidationException.class, () -> controller.updateFilm(film2));
        assertEquals("Такой фильм не существует.", exception.getReason());
    }
}