package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQueryGetAllUsers = "SELECT * FROM USERS ORDER BY USER_ID";
        return jdbcTemplate.query(sqlQueryGetAllUsers, this::mapRowToUser);
    }

    @Override
    @Transactional
    public User getUser(long userId) {
        String sqlQueryGetUser = "select * from USERS where user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQueryGetUser, this::mapRowToUser, userId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public User createUser(User user) throws ValidationException {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");
        return getUser(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        String sqlQueryUpdateUser = "update USERS " +
                "set USER_NAME = ?," +
                "USER_LOGIN = ?," +
                "USER_EMAIL = ?," +
                "USER_BIRTHDAY = ?" +
                "where USER_ID = ?";
        try {
            jdbcTemplate.update(sqlQueryUpdateUser,
                    user.getName(),
                    user.getLogin(),
                    user.getEmail(),
                    user.getBirthday(),
                    user.getId());
        } catch (DataAccessException e) {
            return null;
        }
        return getUser(user.getId());
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sqlQueryAddFriend = "insert into USER_FRIENDS (USER_ID, FRIEND_ID) " +
                " values ( ?, ? )";
        jdbcTemplate.update(sqlQueryAddFriend, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sqlQueryDeleteFriend = "delete from USER_FRIENDS " +
                "where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sqlQueryDeleteFriend, userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        String sqlQueryGetFriends = "select U.USER_ID as user_id,\n" +
                "       U.USER_NAME as user_name,\n" +
                "       U.USER_EMAIL as user_email,\n" +
                "       U.USER_BIRTHDAY as user_birthday,\n" +
                "       U.USER_LOGIN as user_login\n" +
                "from USERS as U\n" +
                "inner join USER_FRIENDS as UF on U.USER_ID = UF.FRIEND_ID\n" +
                "where UF.USER_ID = ?";
        return jdbcTemplate.query(sqlQueryGetFriends, this::mapRowToUser, userId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .name(resultSet.getString("user_name"))
                .login(Objects.requireNonNull(resultSet.getString("user_login")))
                .email(Objects.requireNonNull(resultSet.getString("user_email")))
                .birthday(resultSet.getDate("user_birthday").toLocalDate())
                .build();
    }
}
