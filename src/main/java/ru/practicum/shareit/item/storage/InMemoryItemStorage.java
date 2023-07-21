package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final HashMap<Long, Item> allItem;
    private final InMemoryUserStorage inMemoryUserStorage;
    private final ItemMapper itemMapper;
    private long actualId = 0;

    @Override
    public ItemDto createItem(long userId, Item item) {
        if (inMemoryUserStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден!");
        }
        item.setId(++actualId);
        item.setUserId(userId);
        allItem.put(item.getId(), item);

        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Item item, long itemId, long userId) {
        Item updatedItem = allItem.get(itemId);

        if (updatedItem.getUserId() != userId) {
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

        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemMapper.toItemDto(allItem.get(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUser(long userId) {
        List<Item> itemsByUser = new ArrayList<>();

        for (Item item : allItem.values()) {
            if (item.getUserId() == userId) {
                itemsByUser.add(item);
            }
        }

        return itemsByUser.stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<Item> foundItems = new ArrayList<>();

        if (text.isBlank()) {
            return new ArrayList<>();
        }
        for (Item item : allItem.values()) {
            if (item.getAvailable()) {
                if (item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                    foundItems.add(item);
                }
            }
        }

        return foundItems.stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }
}
