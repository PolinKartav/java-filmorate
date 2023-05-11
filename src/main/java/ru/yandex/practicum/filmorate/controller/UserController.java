package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.ValidationException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    private HashMap<Long, User> users = new HashMap<>();

    @GetMapping()
    public List<User> getAllUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {

        return userService.getUser(id);
    }

    @PostMapping()
    public User createUser(@RequestBody User user) {
        return userService.createUser(checkUser(user));
    }

    @PutMapping()
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(checkUser(user));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable("id") long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(("id")) long id, @PathVariable("otherId") long otherId) {
        User user = userService.getUser(id);
        User friend = userService.getUser(otherId);
        if (user.equals(checkUser(user)) && friend.equals(checkUser(friend))) {
            return userService.getCommonFriends(id, otherId);
        } else throw new ValidationException("Пользователи не верны!");

    }

    private User checkUser(User user) {
        if (user == null) {
            throw new ValidationException("Не заполнены данные для создания пользователя.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Email пользователя пуст или некорректно введен.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Login пустой или содержит пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рожденья не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }
}
