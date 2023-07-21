package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    UserDto createUser(User user);

    List<UserDto> showAllUsers();

    UserDto getUserById(long id);

    void deleteUser(long id);

    UserDto updateUser(User user, long id);
}
