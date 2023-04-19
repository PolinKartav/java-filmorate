package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.ValidationException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private HashMap<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @GetMapping()
    public Collection<Film> findAllFilms() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping()
    public Film createFilm(@RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Фильм уже существует");
        } else {
            Film checkedFilm = checkFilm(film);
            checkedFilm.setId(id);
            films.put(id, checkedFilm);
            id++;
            return checkedFilm;
        }
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            Film checkedFilm = checkFilm(film);
            films.put(film.getId(), checkedFilm);
            return checkedFilm;
        } else {
            throw new ValidationException(HttpStatus.NOT_FOUND, "Такого фильма не существует.");
        }
    }

    public Film checkFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Нет названия фильма.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Описание фильма не дожно превышать 200 символов.");
        }
        LocalDate theEarliestFilm = LocalDate.parse("1895-12-12");
        if (film.getReleaseDate().isBefore(theEarliestFilm)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Релиз фильма не может быть опубликован раньше 28.12.1895.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Продолжительность фильма не может быть 0.");
        }
        return film;
    }
}
