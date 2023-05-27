package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
public class Film {

    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Mpa mpa;
    private Set<Genre> genres;

    public Film(long id, String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa) {
        
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.mpa = mpa;
        genres = new LinkedHashSet<>();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("FILM_NAME", this.name);
        result.put("FILM_DESCRIPTION", this.description);
        result.put("FILM_RELEASE_DATE", this.releaseDate);
        result.put("FILM_DURATION", this.duration);
        result.put("RATING_ID", this.mpa.getId());

        return result;
    }
}
