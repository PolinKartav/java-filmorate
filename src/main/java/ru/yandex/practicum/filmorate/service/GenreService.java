package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenre(int id) {
        return genreStorage.getGenre(id).orElseThrow(() ->
                new GenreNotFoundException(String.format(
                        "Жанр с ID = %s не найден", id
                )
                ));
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }
}
