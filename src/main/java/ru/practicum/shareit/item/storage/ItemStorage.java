package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    ItemDto createItem(long userId, Item item);

    ItemDto updateItem(Item item, long id, long userId);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItemsByUser(long userId);

    List<ItemDto> searchItem(String text);
}
