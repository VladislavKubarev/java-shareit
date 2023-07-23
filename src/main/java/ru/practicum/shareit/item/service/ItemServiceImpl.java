package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemStorage inMemoryItemStorage;
    private final InMemoryUserStorage inMemoryUserStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = inMemoryUserStorage.getUserById(userId);
        Item item = itemMapper.toItem(itemDto, user);

        return itemMapper.toItemDto(inMemoryItemStorage.createItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        User user = inMemoryUserStorage.getUserById(userId);
        Item updatedItem = itemMapper.toItem(itemDto, user);

        return itemMapper.toItemDto(inMemoryItemStorage.updateItem(updatedItem, itemId, userId));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemMapper.toItemDto(inMemoryItemStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUser(long userId) {
        return inMemoryItemStorage.getItemsByUser(userId).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return inMemoryItemStorage.searchItem(text.toLowerCase()).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }
}
