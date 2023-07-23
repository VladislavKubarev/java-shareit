package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final HashMap<Long, Item> allItem;
    private long actualId = 0;

    @Override
    public Item createItem(Item item) {
        item.setId(++actualId);
        allItem.put(item.getId(), item);

        return item;
    }

    @Override
    public Item updateItem(Item item, long itemId, long userId) {
        Item updatedItem = allItem.get(itemId);

        if (updatedItem.getOwner().getId() != userId) {
            throw new NotFoundException("Пользователь не является хозяином этой вещи!");
        }
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }

        return updatedItem;
    }

    @Override
    public Item getItemById(long itemId) {
        return allItem.get(itemId);
    }

    @Override
    public List<Item> getItemsByUser(long userId) {
        List<Item> itemsByUser = new ArrayList<>();

        for (Item item : allItem.values()) {
            if (item.getOwner().getId() == userId) {
                itemsByUser.add(item);
            }
        }

        return itemsByUser;
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> foundItems = new ArrayList<>();

        if (text.isBlank()) {
            return new ArrayList<>();
        }
        for (Item item : allItem.values()) {
            if (item.getAvailable()) {
                if (item.getDescription().toLowerCase().contains(text)) {
                    foundItems.add(item);
                }
            }
        }

        return foundItems;
    }
}
