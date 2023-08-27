package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Component
public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());

        return itemRequest;
    }

    public static ItemResponseDto toItemResponseDto(ItemRequest itemRequest) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(itemRequest.getId());
        itemResponseDto.setDescription(itemRequest.getDescription());
        itemResponseDto.setCreated(itemRequest.getCreated());

        return itemResponseDto;
    }
}
