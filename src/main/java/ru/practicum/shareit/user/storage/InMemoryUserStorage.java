package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> allUsers;
    private final UserMapper userMapper;
    private long actualId = 0;

    @Override
    public UserDto createUser(User user) {
        duplicateValidation(user, user.getId());
        user.setId(++actualId);
        allUsers.put(user.getId(), user);

        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> showAllUsers() {
        return allUsers.values().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long userId) {
        if (!allUsers.containsKey(userId)) {
            throw new NotFoundException("Пользователь не найден!");
        }

        return userMapper.toUserDto(allUsers.get(userId));
    }

    @Override
    public void deleteUser(long userId) {
        allUsers.remove(userId);
    }

    @Override
    public UserDto updateUser(User user, long userId) {
        duplicateValidation(user, userId);
        User updatedUser = allUsers.get(userId);

        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }

        return userMapper.toUserDto(updatedUser);
    }

    private void duplicateValidation(User user, long userId) {
        for (User user1 : allUsers.values()) {
            if (user1.getEmail().equals(user.getEmail()) && user1.getId() != userId) {
                throw new DuplicateEmailException("Пользователь с такой почтой уже существует!");
            }
        }
    }
}
