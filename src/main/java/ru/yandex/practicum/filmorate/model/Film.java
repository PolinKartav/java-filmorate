package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
@AllArgsConstructor
@Data
public class Film {

    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

    /*public Film(int id, String name, String description, LocalDate time, int duration){
        this.id =id;
        this.name = name;
        this.description= description;
        this.releaseDate = time;
        this.duration = duration;


    }*/
}
