package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private long id;
    private String name;
    @Email(message = "Введен некорректный email!")
    @NotBlank(message = "Email не может быть пустым!")
    private String email;
}
