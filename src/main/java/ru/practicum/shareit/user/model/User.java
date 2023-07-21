package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class User {
    private long id;
    private String name;
    @Email(message = "Введен некорректный email!")
    @NotBlank(message = "Email не может быть пустым!")
    private String email;
}
