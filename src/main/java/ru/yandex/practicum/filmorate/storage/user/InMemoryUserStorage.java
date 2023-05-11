package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {

    private HashMap<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public List<User> getAllUsers() {
        if (users.isEmpty()) {
            return null;
        } else {
            List<User> allUsers = new ArrayList(users.values());
            return allUsers;
        }
    }

    @Override
    public User getUser(long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else return null;
    }

    @Override
    public User createUser(User user) {
        if (users.containsKey(user.getId())) {
            return null;
        } else {
            user.setId(id);
            users.put(id, user);
            id++;
            return user;
        }
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            return null;
        }
    }
}
