package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> allUsers;
    private long actualId = 0;

    @Override
    public User createUser(User user) {
        duplicateValidation(user, user.getId());
        user.setId(++actualId);
        allUsers.put(user.getId(), user);

        return user;
    }

    @Override
    public List<User> showAllUsers() {
        return new ArrayList<>(allUsers.values());
    }

    @Override
    public User getUserById(long userId) {
        if (!allUsers.containsKey(userId)) {
            throw new NotFoundException("Пользователь не найден!");
        }

        return allUsers.get(userId);
    }

    @Override
    public void deleteUser(long userId) {
        allUsers.remove(userId);
    }

    @Override
    public User updateUser(User user, long userId) {
        duplicateValidation(user, userId);
        User updatedUser = allUsers.get(userId);

        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }

        return updatedUser;
    }

    private void duplicateValidation(User user, long userId) {
        for (User user1 : allUsers.values()) {
            if (user1.getEmail().equals(user.getEmail()) && user1.getId() != userId) {
                throw new DuplicateEmailException("Пользователь с такой почтой уже существует!");
            }
        }
    }
}
