package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Item {
    private long id;
    private long userId;
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @NotBlank(message = "Описание не может быть пустым!")
    private String description;
    @NotNull(message = "Статус не может быть пустым!")
    private Boolean available;
    private String owner;
    private String request;
}
