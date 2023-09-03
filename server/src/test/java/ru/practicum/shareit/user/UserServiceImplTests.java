package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void createValidUserTest() {
        User testUser = createUser(1, "Владислав", "vlad@yandex.ru");
        UserDto testUserDto = new UserDto();

        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto actualUser = userServiceImpl.saveUser(testUserDto);

        assertThat(actualUser.getId(), equalTo(testUser.getId()));
        assertThat(actualUser.getName(), equalTo(testUser.getName()));
        assertThat(actualUser.getEmail(), equalTo(testUser.getEmail()));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUserWithDuplicateEmailTest() {
        UserDto testUserDto = new UserDto();

        when(userRepository.save(any(User.class))).thenThrow(new DuplicateEmailException("Пользователь с таким email уже существует!"));

        DuplicateEmailException exception = assertThrows(DuplicateEmailException.class,
                () -> userServiceImpl.saveUser(testUserDto));

        assertThat("Пользователь с таким email уже существует!", equalTo(exception.getMessage()));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getAllUsersWhenThereAreUsersTest() {
        List<User> userList = List.of(createUser(1, "Владислав", "vlad@yandex.ru"),
                createUser(2, "Иван", "ivan@yandex.ru"));

        when(userRepository.findAll()).thenReturn(userList);

        List<UserDto> actualUserDtoList = userServiceImpl.findAllUsers();

        assertThat(actualUserDtoList.get(0).getId(), equalTo(userList.get(0).getId()));
        assertThat(actualUserDtoList.get(0).getName(), equalTo(userList.get(0).getName()));
        assertThat(actualUserDtoList.get(0).getEmail(), equalTo(userList.get(0).getEmail()));

        assertThat(actualUserDtoList.get(1).getId(), equalTo(userList.get(1).getId()));
        assertThat(actualUserDtoList.get(1).getName(), equalTo(userList.get(1).getName()));
        assertThat(actualUserDtoList.get(1).getEmail(), equalTo(userList.get(1).getEmail()));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findUserByIdWhenUserIsFoundTest() {
        long id = 1;
        User testUser = createUser(id, "Владислав", "vlad@yandex.ru");

        when(userRepository.findById(id)).thenReturn(Optional.of(testUser));

        UserDto actualUser = userServiceImpl.findUserById(id);

        assertThat(actualUser.getId(), equalTo(testUser.getId()));
        assertThat(actualUser.getName(), equalTo(testUser.getName()));
        assertThat(actualUser.getEmail(), equalTo(testUser.getEmail()));

        verify(userRepository, times(1)).findById(testUser.getId());
    }

    @Test
    void findUserByIdWhenUserIsNotFoundTest() {
        long id = 0;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userServiceImpl.findUserById(id));

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void deleteUserTest() {
        long id = 1;

        userServiceImpl.deleteUser(id);

        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void updateValidUserTest() {
        long id = 1;
        User oldTestUser = createUser(id, "Владислав", "vlad@yandex.ru");
        User newTestUser = createUser(id, "Иван", "ivan@yandex.ru");
        UserDto newTestUserDto = new UserDto();

        when(userRepository.findById(oldTestUser.getId())).thenReturn(Optional.of(oldTestUser));
        when(userRepository.save(any(User.class))).thenReturn(newTestUser);

        UserDto actualUser = userServiceImpl.updateUser(newTestUserDto, id);

        assertThat(actualUser.getId(), equalTo(newTestUser.getId()));
        assertThat(actualUser.getName(), equalTo(newTestUser.getName()));
        assertThat(actualUser.getEmail(), equalTo(newTestUser.getEmail()));

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserWhenUserIsNotFound() {
        long id = 0;
        UserDto newTestUserDto = new UserDto();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userServiceImpl.updateUser(newTestUserDto, id));

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).save(any(User.class));
    }

    private User createUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }
}
