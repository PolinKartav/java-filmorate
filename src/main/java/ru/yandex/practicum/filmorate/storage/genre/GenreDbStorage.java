package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQueryGetAllGenres = "SELECT GENRE_NAME FROM GENRE ORDER BY GENRE_ID";
        return jdbcTemplate.query(sqlQueryGetAllGenres, this::mapRowToGenre);
    }

    @Override
    @Transactional
    public Optional<Genre> getGenre(int genreId) {
        String sqlQueryGetGenre = "select GENRE_NAME from GENRE where GENRE_ID = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sqlQueryGetGenre, this::mapRowToGenre, genreId));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.valueOf(resultSet.getString("GENRE_NAME"));
    }
}
