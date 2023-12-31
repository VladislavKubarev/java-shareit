package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item.getRequest() == null) {
            itemDto.setRequestId(null);
        } else {
            itemDto.setRequestId(item.getRequest().getId());
        }

        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);

        return item;
    }

    public static ItemWithBookingAndCommentsDto itemWithBookingAndCommentsDto(Item item) {
        ItemWithBookingAndCommentsDto itemWithBookingAndCommentsDto = new ItemWithBookingAndCommentsDto();
        itemWithBookingAndCommentsDto.setId(item.getId());
        itemWithBookingAndCommentsDto.setName(item.getName());
        itemWithBookingAndCommentsDto.setDescription(item.getDescription());
        itemWithBookingAndCommentsDto.setAvailable(item.getAvailable());

        return itemWithBookingAndCommentsDto;
    }
}
