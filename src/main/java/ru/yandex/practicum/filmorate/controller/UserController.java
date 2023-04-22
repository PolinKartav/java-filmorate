package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.ValidationException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;


@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private HashMap<Integer, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping()
    public Collection<User> findAllUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    @PostMapping()
    public User createUser(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            log.warn("Пользователь уже существует");
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Пользователь уже существует");
        } else {
            User checkedUser = checkUser(user);
            checkedUser.setId(id);
            users.put(id, checkedUser);
            id++;
            log.info("Пользователь создан: ", checkedUser);
            return checkedUser;
        }
    }

    @PutMapping()
    public User updateUser(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            User checkedUser = checkUser(user);
            users.put(user.getId(), checkedUser);
            log.info("Пользователь обновлен: ", checkedUser);
            return checkedUser;
        } else {
            log.warn("Такого пользователя не существует");
            throw new ValidationException(HttpStatus.NOT_FOUND, "Такого пользователя не существует");
        }
    }

    private User checkUser(User user) {
        if (user == null){
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Не заполнены данные для создания пользователя.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Email пользователя пуст или некорректно введен.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Login пустой или содержит пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Дата рожденья не может быть в будущем.");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return user;
    }
}
