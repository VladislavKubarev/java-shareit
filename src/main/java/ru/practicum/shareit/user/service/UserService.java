package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    List<UserDto> showAllUsers();

    UserDto getUserById(long userId);

    void deleteUser(long userId);

    UserDto updateUser(UserDto userDto, long userId);
}
