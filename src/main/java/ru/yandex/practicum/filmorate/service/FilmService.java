package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.validation.FilmNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film getFilm(long filmId) {
        if (filmStorage.getFilm(filmId) != null) {
            log.info("Фильм по ID %s: ", filmStorage.getFilm(filmId));
            return filmStorage.getFilm(filmId);
        } else log.warn("Фильма с ID %s не существует!");
        throw new FilmNotFoundException("Фильма с ID %s% не существует!");
    }

    public List<Film> getAllFilms() {
        if (filmStorage.getAllFilms() != null) {
            log.info("Список фильмов: ", filmStorage.getAllFilms(), "!");
            return filmStorage.getAllFilms();
        } else log.warn("Список фильмов пуст!");
        return new ArrayList<>();
    }

    public Film createFilm(Film film) {
        Film newFilm = filmStorage.createFilm(film);
        if (newFilm != null) {
            log.info("Фильм создан: ", film);
            return newFilm;
        } else log.warn("Такой фильм уже существует.");
        throw new FilmAlreadyExistsException("Такой фильм уже существует.");
    }

    public Film updateFilm(Film film) {
        Film newFilm = filmStorage.updateFilm(film);
        if (newFilm != null) {
            log.info("Фильм обновлен: ", film);
            return newFilm;
        } else log.warn("Такой фильм не существует.");
        throw new FilmNotFoundException("Такой фильм не существует.");
    }

    //Метод добавляет пользовательский лайк фильму.
    public void addLike(long filmId, long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            log.warn("Фильм с таким ID {} не существует.", filmId);
            throw new FilmAlreadyExistsException("Фильм с таким ID %s не существует.");
        }
        if (userStorage.getUser(userId) == null) {
            log.warn("Пользователя  с таким ID {} не существует!", userId);
            throw new FilmAlreadyExistsException("Пользователя  с таким ID %s не существует!");
        }
        filmStorage.getFilm(filmId).getLikes().add(userId);
    }

    //Метод выводит 10 самых популярных фильмов.
    public Set<Film> getPopularFilm(Integer count) {
        if (count == null) {
            count = 10;
        }
        return filmStorage.getAllFilms()
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(film -> film.getLikes().size())))
                .limit(count)
                .collect(Collectors.toSet());
    }

    //Метод удаляет пользовательский лайк у фильма.
    public void removeLike(long filmId, long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            log.warn("Фильм с таким ID %s не существует.");
            throw new FilmNotFoundException("Фильм с таким ID %s не существует.");
        }
        if (userStorage.getUser(userId) == null) {
            log.warn("Пользователя  с таким ID %s не существует!");
            throw new FilmNotFoundException("Пользователя  с таким ID %s не существует!");
        }
        filmStorage.getFilm(filmId).getLikes().remove(userId);
    }
}
