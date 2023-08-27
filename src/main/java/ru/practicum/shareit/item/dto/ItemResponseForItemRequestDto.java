package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseForItemRequestDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long requestId;
}