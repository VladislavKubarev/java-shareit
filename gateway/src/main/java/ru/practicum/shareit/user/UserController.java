package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен POST-запрос на добавление пользователя");
        return userClient.saveUser(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        log.info("Получен GET-запрос на получение списка всех пользователей");
        return userClient.findAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable long userId) {
        log.info("Получен GET-запрос на получение пользователя с ID={}", userId);
        return userClient.findUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("Получен DELETE-запрос на удаление пользователя с ID={}", userId);
        return userClient.deleteUser(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Получен PATCH-запрос на обновление пользователя с ID={}", userId);
        return userClient.updateUser(userDto, userId);
    }
}
