package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQueryGetAllFilms = "select * from FILMS " +
                "left join RATING on FILMS.RATING_ID = RATING.RATING_ID " +
                "order by FILM_ID ";
        List<Film> films = jdbcTemplate.query(sqlQueryGetAllFilms, this::mapRowToFilm);
        Map<Long, Film> mapFilms = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        String sqlQueryGetAllGenres = "select FG.FILM_ID as filmId,\n" +
                "       G2.GENRE_NAME as genreName,\n" +
                "       G2.GENRE_ID as genreId\n" +
                "from FILMS_GENRES FG\n" +
                "    left join GENRE G2 on FG.GENRE_ID = G2.GENRE_ID\n" +
                "order by genreId\n";
        List<Map<String, Object>> genres = jdbcTemplate.queryForList(sqlQueryGetAllGenres);
        genres.forEach(t -> mapFilms.get(Long.parseLong(t.get("filmId").toString()))
                .getGenres()
                .add(Genre.valueOf(t.get("genreName").toString())
                ));
        return films;
    }

    @Override
    @Transactional
    public Film getFilm(long filmId) {
        String sqlQueryGetFilm = "select * from films " +
                "left join RATING on FILMS.RATING_ID = RATING.RATING_ID " +
                "where FILM_ID = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQueryGetFilm, this::mapRowToFilm, filmId);
        } catch (DataAccessException e) {
            return null;
        }
        assert film != null;
        String sqlQueryGetGenres = "select GENRE_NAME as genre\n" +
                "from FILMS_GENRES FG\n" +
                "         left join GENRE G on G.GENRE_ID = FG.GENRE_ID\n" +
                "where FILM_ID = ?\n" +
                "order by G.GENRE_ID";
        List<Genre> genresFilms = jdbcTemplate.query(sqlQueryGetGenres, this::mapRowToGenre, filmId);
        film.setGenres(new LinkedHashSet<>(genresFilms));
        return film;
    }

    @Override
    @Transactional
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();

        String sqlQueryAddGenres = "insert into FILMS_GENRES (FILM_ID, GENRE_ID)\n" +
                "values (?, ?)";

        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(sqlQueryAddGenres, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setInt(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
        return getFilm(filmId);
    }

    @Override
    @Transactional
    public Film updateFilm(Film film) {
        String sqlQueryUpdateFilm = "update FILMS " +
                "set FILM_NAME = ?," +
                "FILM_DESCRIPTION = ?," +
                "FILM_DURATION = ?," +
                "FILM_RELEASE_DATE = ?," +
                "RATING_ID = ? " +
                "where FILM_ID = ?";
        try {
            jdbcTemplate.update(sqlQueryUpdateFilm,
                    film.getName(),
                    film.getDescription(),
                    film.getDuration(),
                    film.getReleaseDate(),
                    film.getMpa().getId(),
                    film.getId());
        } catch (DataAccessException e) {
            return null;
        }

        String sqlQueryDeleteGenres = "delete from FILMS_GENRES\n" +
                "where FILM_ID = ?";

        jdbcTemplate.update(sqlQueryDeleteGenres, film.getId());

        String sqlQueryAddGenres = "insert into FILMS_GENRES (FILM_ID, GENRE_ID)\n" +
                "values (?, ?)";

        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(sqlQueryAddGenres, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setInt(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });

        return getFilm(film.getId());
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQueryGetPopularFilms = "select F.FILM_ID as film_id,\n" +
                "       FILM_DESCRIPTION as film_description,\n" +
                "       FILM_NAME as film_name,\n" +
                "       FILM_RELEASE_DATE as film_release_date,\n" +
                "       FILM_DURATION as film_duration,\n" +
                "       R.RATING_NAME as rating_name\n" +
                "from FILMS F\n" +
                "left join FILM_LIKES FL on FL.FILM_ID = F.FILM_ID\n" +
                "left join RATING R on R.RATING_ID = F.RATING_ID\n" +
                "group by F.FILM_ID, " +
                "   FILM_DESCRIPTION, " +
                "   FILM_NAME, " +
                "   FILM_RELEASE_DATE, " +
                "   FILM_DURATION, " +
                "   R.RATING_NAME\n" +
                "order by count(FL.USER_ID) desc\n" +
                "limit ?";
        List<Film> films = jdbcTemplate.query(sqlQueryGetPopularFilms, this::mapRowToFilm, count);
        if (!films.isEmpty()) {
            Map<Long, Film> mapFilms = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
            String sqlQueryGetAllGenres = "select FG.FILM_ID as filmId,\n" +
                    "       G2.GENRE_NAME as genreName,\n" +
                    "       G2.GENRE_ID as genreId\n" +
                    "from FILMS_GENRES FG\n" +
                    "    left join GENRE G2 on FG.GENRE_ID = G2.GENRE_ID\n" +
                    "order by genreId\n";
            List<Map<String, Object>> genres = jdbcTemplate.queryForList(sqlQueryGetAllGenres);
            genres.forEach(t -> mapFilms.get(Long.parseLong(t.get("filmId").toString()))
                    .getGenres()
                    .add(Genre.valueOf(t.get("genreName").toString())
                    ));
        }

        return films;
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sqlQueryAddLikeToFilm = "insert into FILM_LIKES (FILM_ID, USER_ID) \n" +
                "values (?, ?)";

        jdbcTemplate.update(sqlQueryAddLikeToFilm, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sqlQueryRemoveLikeFromFilm = "delete from FILM_LIKES\n" +
                "where FILM_ID = ? AND USER_ID = ?";

        jdbcTemplate.update(sqlQueryRemoveLikeFromFilm, filmId, userId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return new Film(
                resultSet.getLong("film_id"),
                Objects.requireNonNull(resultSet.getString("film_name")),
                resultSet.getString("film_description"),
                Objects.requireNonNull(resultSet.getDate("film_release_date")).toLocalDate(),
                resultSet.getInt("film_duration"),
                Mpa.valueOf(resultSet.getString("rating_name")));
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.valueOf(resultSet.getString("GENRE_NAME"));
    }
}
