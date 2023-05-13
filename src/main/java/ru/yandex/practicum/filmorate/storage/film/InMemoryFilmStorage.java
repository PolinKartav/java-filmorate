package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private HashMap<Long, Film> films = new HashMap<>();
    private long id = 1;


    @Override
    public List<Film> getAllFilms() {
        if (films.isEmpty()) {
            return null;
        } else {
            List<Film> allFilms = new ArrayList(films.values());
            return allFilms;
        }
    }

    @Override
    public Film getFilm(long filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else return null;
    }

    @Override
    public Film createFilm(Film film) {
        if (films.containsKey(film.getId())) {
            return null;
        } else {
            film.setId(id);
            films.put(id, film);
            id++;
            return film;
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else return null;
    }
}
