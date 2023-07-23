package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItem(Item item, long id, long userId);

    Item getItemById(long itemId);

    List<Item> getItemsByUser(long userId);

    List<Item> searchItem(String text);
}
