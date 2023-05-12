package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.validation.UserNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
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

    //Метод создания пользователя.
    public User createUser(User user) {
        User newUser = userStorage.createUser(user);
        if (newUser != null) {
            log.info("Пользователь создан: {}", user);
            return newUser;
        } else log.warn("Пользователь уже существует");
        throw new UserAlreadyExistsException("Пользователь уже существует");

    }

    //Метод обновления пользователя.
    public User updateUser(User user) {
        User newUser = userStorage.updateUser(user);
        if (newUser != null) {
            log.info("Пользователь обновлен: {}", user);
            return newUser;
        } else log.warn("Такого пользователя не существует");
        throw new UserNotFoundException("Такого пользователя не существует");
    }

    //Метод добавляет пользователю в друзей друга.
    public void addFriend(long userId, long friendId) {
        if (userStorage.getUser(userId) == null) {
            log.warn("Такого пользователя не существует");
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        if (userStorage.getUser(friendId) == null) {
            log.warn("Такого пользователя не существует");
            throw new UserNotFoundException("Такого друга не существует");
        }

        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    //Метод удаляет друга из друзей пользователя.
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
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(@PathVariable("id") long id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            log.warn("Пользователя  с таким ID {} не существует!", id);
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        return user.getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    //Метод возвращает коллекцию общих друзей между одним пользователем и другим.
    public List<User> getCommonFriends(long userId, long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if (user == null) {
            log.warn("Пользователя  с таким ID {} не существует!", userId);
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        if (friend == null) {
            log.warn("Пользователя  с таким ID {} не существует!", friendId);
            throw new UserNotFoundException("Такого друга не существует");
        }
        Set<User> userFriends = user.getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toSet());

        Set<User> friendsFriends = friend.getFriends().stream()
                .map(t -> userStorage.getUser(t))
                .collect(Collectors.toSet());

        userFriends.retainAll(friendsFriends);

        return userFriends.stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());
    }
}
