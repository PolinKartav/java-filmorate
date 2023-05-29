package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAllFilms();

    Optional<Film> getFilm(long filmId);

    Optional<Film> createFilm(Film film);

    Optional<Film> updateFilm(Film film);

    List<Film> getPopularFilms(int count);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);
}
