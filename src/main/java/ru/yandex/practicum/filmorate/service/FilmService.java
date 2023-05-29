package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film getFilm(long filmId) {
        return filmStorage.getFilm(filmId).orElseThrow(() ->
                new FilmNotFoundException("Фильма не существует!"));
    }

    public List<Film> getAllFilms() {
        if (!filmStorage.getAllFilms().isEmpty()) {
            return filmStorage.getAllFilms();
        } else log.warn("Список фильмов пуст!");
        return new ArrayList<>();
    }

    public Film createFilm(Film film) {
        Film newFilm = filmStorage.createFilm(film).orElseThrow(() ->
                new FilmAlreadyExistsException("Такой фильм уже существует."));
        log.info("Фильм создан: {}", film);
        return newFilm;
    }

    public Film updateFilm(Film film) {
        Film newFilm = filmStorage.updateFilm(film).orElseThrow(() ->
                new FilmNotFoundException("Такой фильм не существует."));
        log.info("Фильм обновлен: {}", film);
        return newFilm;
    }

    public void addLike(long filmId, long userId) {
        filmStorage.getFilm(filmId).orElseThrow(() ->
                new FilmAlreadyExistsException("Фильм с таким ID {} не существует."));
        userStorage.getUser(userId).orElseThrow(() ->
                new FilmAlreadyExistsException("Пользователя  с таким ID {} не существует!"));
        filmStorage.addLike(filmId, userId);
    }

    public List<Film> getPopularFilm(Integer count) {
        if (count == null) {
            count = 10;
        }
        return filmStorage.getPopularFilms(count);
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.getFilm(filmId).orElseThrow(() ->
                new FilmNotFoundException("Фильм с таким ID  не существует."));
        userStorage.getUser(userId).orElseThrow(() ->
                new FilmNotFoundException("Пользователя  с таким ID не существует!"));
        filmStorage.removeLike(filmId, userId);
    }
}
