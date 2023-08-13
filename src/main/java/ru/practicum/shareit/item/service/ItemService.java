package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(long userId, ItemDto itemDto);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemWithBookingAndCommentsDto findItemById(long itemId, long userId);

    List<ItemWithBookingAndCommentsDto> findItemsByUser(long userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(long userId, CommentDto commentDto, long itemId);
}
