package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto saveUser(UserDto userDto);

    List<UserDto> findAllUsers();

    UserDto findUserById(long userId);

    void deleteUser(long userId);

    UserDto updateUser(UserDto userDto, long userId);
}
