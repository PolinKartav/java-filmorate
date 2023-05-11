package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {

    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private List<String> filmsTop;
    private Set<Long> likes = new HashSet<>();
}
