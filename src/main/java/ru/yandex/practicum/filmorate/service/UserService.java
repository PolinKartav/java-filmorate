package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        if (userStorage.getAllUsers() != null) {
            log.info("Список пользователей: {}", userStorage.getAllUsers());
            return userStorage.getAllUsers();
        } else log.warn("Список пользователей пуст!");
        return new ArrayList<>();
    }

    public User getUser(long userId) {
        if (userStorage.getUser(userId) != null) {
            log.info("Пользователь с ID {} : ", userId);
            return userStorage.getUser(userId);
        } else log.warn("Такого пользователя не существует");
        throw new UserNotFoundException("Такого пользователя не существует");
    }

    public User createUser(User user) {
        normalizeUser(user);
        User newUser = userStorage.createUser(user);
        if (newUser != null) {
            log.info("Пользователь создан: {}", user);
            return newUser;
        } else {
            log.warn("Пользователь уже существует");
        }
        throw new UserAlreadyExistsException("Пользователь уже существует");
    }

    public User updateUser(User user) {
        normalizeUser(user);
        if (userStorage.getUser(user.getId()) == null) {
            throw new UserNotFoundException(String.format("Пользователь с ID = %s не существует", user.getId()));
        }
        User newUser = userStorage.updateUser(user);
        log.info("Пользователь обновлен: {}", user);
        return newUser;
    }

    public void addFriend(long userId, long friendId) {
        if (userStorage.getUser(userId) == null) {
            log.warn("Такого пользователя не существует");
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        if (userStorage.getUser(friendId) == null) {
            log.warn("Такого пользователя не существует");
            throw new UserNotFoundException("Такого друга не существует");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if (user == null) {
            log.warn("Такого пользователя не существует");
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        if (friend == null) {
            log.warn("Такого пользователя не существует");
            throw new UserNotFoundException("Такого друга не существует");
        }
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(@PathVariable("id") long id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            log.warn("Пользователя  с таким ID {} не существует!", id);
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        if (userStorage.getUser(userId) == null) {
            log.warn("Пользователя  с таким ID {} не существует!", userId);
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        if (userStorage.getUser(friendId) == null) {
            log.warn("Пользователя  с таким ID {} не существует!", friendId);
            throw new UserNotFoundException("Такого друга не существует");
        }
        List<User> userFriends = userStorage.getFriends(userId);

        List<User> friendFriends = userStorage.getFriends(friendId);

        if (userFriends.isEmpty() || friendFriends.isEmpty()) {
            return new ArrayList<>();
        }
        userFriends.retainAll(friendFriends);
        return userFriends.stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());
    }

    private void normalizeUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
