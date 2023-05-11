package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController controller = new UserController();

    @Test
    void createUserWithIncorrectName() {
        User user = new User(1, "email@mail.com", "bukaka", "", LocalDate.parse("1999-10-23"));
        controller.createUser(user);
        assertEquals("", user.getName());
    }

    @Test
    void createUserWithCorrectFields() {
        User user = new User(1, "email@mail.com", "bukaka", "Charly", LocalDate.parse("1999-10-23"));
        assertEquals(user, controller.createUser(user));
    }

    @Test
    void createUserWithIncorrectEmail() {
        User user = new User(1, "emailmail.com", "bukaka", "Charly", LocalDate.parse("1999-10-23"));
        final ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        assertEquals("Email пользователя пуст или некорректно введен.", exception.getReason());
    }

    @Test
    void createUserWithIncorrectLogin() {
        User user = new User(1, "email@mail.com", "", "Charly", LocalDate.parse("1999-10-23"));
        final ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        assertEquals("Login пустой или содержит пробелы.", exception.getReason());
    }

    @Test
    void createUserWithIncorrectBirthday() {
        User user = new User(1, "email@mail.com", "bukaka", "Charly", LocalDate.parse("2023-12-23"));
        final ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        assertEquals("Дата рожденья не может быть в будущем.", exception.getReason());
    }

    @Test
    void updateUser() {
        User user = new User(1, "email@mail.com", "bukaka", "Charly", LocalDate.parse("2003-10-23"));
        controller.createUser(user);
        User user2 = new User(1, "email@mail.com", "bukaka", "Frensys", LocalDate.parse("1998-12-12"));
        assertEquals(user2, controller.updateUser(user2));
    }

    @Test
    void updateUserWithIncorrectId() {
        User user = new User(1, "email@mail.com", "bukaka", "Charly", LocalDate.parse("2003-10-23"));
        controller.createUser(user);
        User user2 = new User(2, "email@mail.com", "bukaka", "Frensys", LocalDate.parse("1998-12-12"));
        final ValidationException exception = assertThrows(ValidationException.class, () -> controller.updateUser(user2));
        assertEquals("Такого пользователя не существует", exception.getReason());
    }
}