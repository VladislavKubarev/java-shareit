package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @Test
    void createUserTest() throws Exception {
        UserDto userDto = createUserDto(1, "Владислав", "vlad@yandex.ru");

        when(userService.saveUser(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).saveUser(any(UserDto.class));
    }

    @Test
    void findAllUsersTest() throws Exception {
        List<UserDto> usersDtoList = List.of(createUserDto(1, "Владислав", "vlad@yandex.ru"),
                createUserDto(2, "Иван", "ivan@yandex.ru"));

        when(userService.findAllUsers()).thenReturn(usersDtoList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(usersDtoList)));

        verify(userService, times(1)).findAllUsers();
    }

    @Test
    void findUserByIdTest() throws Exception {
        long id = 1;
        UserDto userDto = createUserDto(id, "Владислав", "vlad@yandex.ru");

        when(userService.findUserById(id)).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", id)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).findUserById(id);
    }

    @Test
    void deleteUserTest() throws Exception {
        long id = 1;
        mockMvc.perform(delete("/users/{userId}", id))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1);
    }

    @Test
    void updateUserTest() throws Exception {
        long id = 1;
        UserDto newUserDto = createUserDto(id, "Иван", "ivan@yandex.ru");

        when(userService.updateUser(any(UserDto.class), eq(id))).thenReturn(newUserDto);

        mockMvc.perform(patch("/users/{userId}", id)
                        .content(objectMapper.writeValueAsString(newUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newUserDto.getId()))
                .andExpect(jsonPath("$.name").value(newUserDto.getName()))
                .andExpect(jsonPath("$.email").value(newUserDto.getEmail()));

        verify(userService, times(1)).updateUser(any(UserDto.class), eq(id));
    }

    private UserDto createUserDto(long id, String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setName(name);
        userDto.setEmail(email);

        return userDto;
    }
}
