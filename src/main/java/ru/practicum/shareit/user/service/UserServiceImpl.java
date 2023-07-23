package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);

        return userMapper.toUserDto(inMemoryUserStorage.createUser(user));
    }

    @Override
    public List<UserDto> showAllUsers() {
        return inMemoryUserStorage.showAllUsers().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long userId) {
        return userMapper.toUserDto(inMemoryUserStorage.getUserById(userId));
    }

    @Override
    public void deleteUser(long userId) {
        inMemoryUserStorage.deleteUser(userId);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User updatedUser = userMapper.toUser(userDto);

        return userMapper.toUserDto(inMemoryUserStorage.updateUser(updatedUser, userId));
    }
}
