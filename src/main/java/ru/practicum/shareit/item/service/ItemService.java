package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItemsByUser(long userId);

    List<ItemDto> searchItem(String text);
}
