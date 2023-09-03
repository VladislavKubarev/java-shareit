package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);

        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Пользователь с таким email уже существует!");
        }
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден!")));
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User updatedUser = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден!"));

        if (userDto.getEmail() != null) {
            updatedUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }

        return UserMapper.toUserDto(userRepository.save(updatedUser));
    }
}
