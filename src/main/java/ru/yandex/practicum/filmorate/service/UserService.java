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
        if (!userStorage.getAllUsers().isEmpty()) {
            log.info("Список пользователей: {}", userStorage.getAllUsers());
            return userStorage.getAllUsers();
        } else log.warn("Список пользователей пуст!");
        return new ArrayList<>();
    }

    public User getUser(long userId) {
        return userStorage.getUser(userId).orElseThrow(() ->
                new UserNotFoundException("Такого пользователя не существует"));
    }

    public User createUser(User user) {
        normalizeUser(user);
        User newUser = userStorage.createUser(user).orElseThrow(() ->
                new UserAlreadyExistsException("Пользователь уже существует"));
        log.info("Пользователь создан: {}", user);
        return newUser;
    }

    public User updateUser(User user) {
        normalizeUser(user);
        userStorage.getUser(user.getId()).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь с ID = %s не существует", user.getId())));

        User newUser = userStorage.updateUser(user).get();
        log.info("Пользователь обновлен: {}", user);
        return newUser;
    }

    public void addFriend(long userId, long friendId) {
        userStorage.getUser(userId).orElseThrow(() ->
                new UserNotFoundException("Такого пользователя не существует"));

        userStorage.getUser(friendId).orElseThrow(() ->
                new UserNotFoundException("Такого друга не существует"));

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        userStorage.getUser(userId).orElseThrow(() ->
                new UserNotFoundException("Такого пользователя не существует"));
        userStorage.getUser(friendId).orElseThrow(() ->
                new UserNotFoundException("Такого друга не существует"));
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(@PathVariable("id") long id) {
        userStorage.getUser(id).orElseThrow(() ->
                new UserNotFoundException("Такого пользователя не существует"));
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        userStorage.getUser(userId).orElseThrow(() ->
                new UserNotFoundException("Такого пользователя не существует"));
        userStorage.getUser(friendId).orElseThrow(() ->
                new UserNotFoundException("Такого друга не существует"));

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
