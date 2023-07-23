package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    List<User> showAllUsers();

    User getUserById(long userId);

    void deleteUser(long userId);

    User updateUser(User user, long userId);
}
