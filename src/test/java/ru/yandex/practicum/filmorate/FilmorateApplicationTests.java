package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    private static User user1;
    private static User user2;
    private static Film film1;
    private static Film film2;

    @BeforeEach
    void createUsersAndFilms() {
        user1 = new User(1L,
                "email@email.ru",
                "HuGo",
                "Hugo",
                LocalDate.of(1990, 12, 12));
        user2 = new User(2L,
                "yandex@yandex.ru",
                "russs",
                "Ruslan",
                LocalDate.of(1995, 5, 5));
        film1 = new Film(1L,
                "HarryPotter",
                "The boy who survived came to die.",
                LocalDate.of(2000, 3, 12),
                152,
                Mpa.G);
        film2 = new Film(2L,
                "It",
                "A clown who has no friends eating children.",
                LocalDate.of(2017, 4, 21),
                135,
                Mpa.NC17);
    }

    @Test
    void testCreateUser(){
        User user = new User(1L,
                "email@email.ru",
                "HuGo",
                "Hugo",
                LocalDate.of(1990, 12, 12));
        assertEquals(user1, userStorage.createUser(user));
    }

    @Test
    void testGetUserById(){
        userStorage.createUser(user1);
        userStorage.createUser(user2);
        assertEquals(user1, userStorage.getUser(1));
        assertEquals(user2, userStorage.getUser(2));
    }

    @Test
    void testGetAllUsers(){
        userStorage.createUser(user1);
        userStorage.createUser(user2);
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        assertEquals(users, userStorage.getAllUsers().stream().toList());
    }

    @Test
    void testUpdateUser(){
        userStorage.createUser(user1);
        User user = new User(1L,
                "hugo@yandex.com",
                "HuGo",
                "Hugo",
                LocalDate.of(1990, 12, 12));
        assertEquals(user, userStorage.updateUser(user));
    }

   @Test
   void testGetFriends(){
       userStorage.createUser(user1);
       userStorage.createUser(user2);
       userStorage.addFriend(user1.getId(), user2.getId());

       List<User> friends = new ArrayList<>();
       friends.add(user2);
       assertEquals(friends, userStorage.getFriends(user1.getId()));
   }

  @Test
   void testCreateFilm(){
        Film film = new  Film(3L,
               "HarryPotter",
               "The boy who survived came to die.",
               LocalDate.of(2000, 3, 12),
               152,
               Mpa.G);
        assertEquals(film.getClass(), filmStorage.createFilm(film).getClass());
   }

   @Test
   void testGetFilmById(){
       filmStorage.createFilm(film1);
       assertEquals(film1.getClass(), filmStorage.getFilm(1).getClass());
   }
   @Test
   void testUpdateFilm(){
        filmStorage.createFilm(film1);
       Film film = new  Film(1L,
               "HarryPotter",
               "The boy who survived came to die.",
               LocalDate.of(2001, 3, 12),
               152,
               Mpa.G);
       assertEquals(film.getClass(), filmStorage.updateFilm(film).getClass());
   }

   @Test
   void testGetPopularFilms(){
       filmStorage.createFilm(film1);
       filmStorage.createFilm(film2);
       List<Film> films = new ArrayList<>();
       films.add(film1);
       films.add(film2);
       assertEquals(films.getClass(), filmStorage.getPopularFilms(2).getClass());
   }
    @Test
    void testGetGenreById() {
        assertEquals(genreStorage.getGenre(1).orElse(null), Genre.COMEDY);
        assertEquals(genreStorage.getGenre(2).orElse(null), Genre.DRAMA);
        assertEquals(genreStorage.getGenre(3).orElse(null), Genre.CARTOON);
        assertEquals(genreStorage.getGenre(4).orElse(null), Genre.THRILLER);
        assertEquals(genreStorage.getGenre(5).orElse(null), Genre.DOCUMENTARY);
        assertEquals(genreStorage.getGenre(6).orElse(null), Genre.ACTION);
    }

    @Test
    void testGetAllGenres(){
        assertEquals(6, genreStorage.getAllGenres().size());
    }

    @Test
    void testGetMpaById() {
        assertEquals(mpaStorage.getMpa(1).orElse(null), Mpa.G);
        assertEquals(mpaStorage.getMpa(2).orElse(null), Mpa.PG);
        assertEquals(mpaStorage.getMpa(3).orElse(null), Mpa.PG13);
        assertEquals(mpaStorage.getMpa(4).orElse(null), Mpa.R);
        assertEquals(mpaStorage.getMpa(5).orElse(null), Mpa.NC17);
    }
    @Test
    void testGetAllMaps(){
        assertEquals(5, mpaStorage.getAllMpa().size());
    }
}
