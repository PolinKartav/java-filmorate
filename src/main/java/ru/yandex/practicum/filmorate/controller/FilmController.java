package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping()
    public List<Film> getAllFilms() {
        log.debug("Текущее количество фильмов: {}", filmService.getAllFilms().size());
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable long id) {
        return filmService.getFilm(id);
    }

    @PostMapping()
    public Film createFilm(@RequestBody Film film) {
        return filmService.createFilm(checkFilm(film));
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(checkFilm(film));
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilm(@RequestParam(required = false) Integer count) {
        return filmService.getPopularFilm(count);
    }

    public Film checkFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Не заполнены данные для создания фильма.");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Нет названия фильма.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не дожно превышать 200 символов.");
        }
        LocalDate theEarliestFilm = LocalDate.parse("1895-12-28");
        if (film.getReleaseDate().isBefore(theEarliestFilm)) {
            throw new ValidationException("Релиз фильма не может быть опубликован раньше 28.12.1895.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма не может быть 0.");
        }
        return film;
    }
}
