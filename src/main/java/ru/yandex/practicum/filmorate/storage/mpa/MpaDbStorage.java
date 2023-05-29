package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQueryGetAllMpa = "SELECT RATING_NAME FROM RATING ORDER BY RATING_ID";
        return jdbcTemplate.query(sqlQueryGetAllMpa, this::mapRowToMpa);
    }

    @Override
    @Transactional
    public Optional<Mpa> getMpa(int mpaId) {
        String sqlQueryGetGenre = "select RATING_NAME from RATING where RATING_ID = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sqlQueryGetGenre, this::mapRowToMpa, mpaId));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.valueOf(resultSet.getString("RATING_NAME"));
    }
}
