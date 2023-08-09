package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingResponseForItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public ItemDto saveItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден!"));

        Item item = itemMapper.toItem(itemDto, user);

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item updatedItem = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь не найдена!"));

        if (updatedItem.getOwner().getId() != userId) {
            throw new NotFoundException("Пользователь не является хозяином этой вещи!");
        }
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.save(updatedItem));
    }

    @Override
    public ItemWithBookingAndCommentsDto findItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь не найдена!"));

        ItemWithBookingAndCommentsDto itemWithBookingDto = itemMapper.itemWithBookingAndCommentsDto(item);

        if (item.getOwner().getId() == userId) {
            itemWithBookingDto.setNextBooking(getNextBooking(itemId));
            itemWithBookingDto.setLastBooking(getLastBooking(itemId));
        }
        itemWithBookingDto.setComments(getCommentByItemId(itemId));

        return itemWithBookingDto;
    }

    @Override
    public List<ItemWithBookingAndCommentsDto> findItemsByUser(long userId) {
        return itemRepository.findByOwnerId(userId).stream().map(item -> findItemById(item.getId(), userId))
                .sorted(Comparator.comparing(ItemWithBookingAndCommentsDto::getId)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (!text.isBlank()) {
            return itemRepository.searchItems(text).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public CommentDto addComment(long userId, CommentDto commentDto, long itemId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден!"));

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь не найдена!"));

        Comment comment = commentMapper.toComment(commentDto, user, item, LocalDateTime.now());

        if (bookingRepository.findByItemIdAndBookerIdAndEndIsBeforeAndStatus(itemId, userId, LocalDateTime.now(),
                BookingStatus.APPROVED).isEmpty()) {
            throw new ValidationException("Невозможно оставить отзыв на вещь, которую вы не бронировали!");
        }

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private BookingResponseForItemDto getNextBooking(long itemId) {
        Booking booking = bookingRepository.findFirstByItemIdAndStartGreaterThanEqualAndStatusOrderByStartAsc(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (booking != null) {
            return bookingMapper.toBookingResponseForItemDto(booking);
        } else {
            return null;
        }
    }

    private BookingResponseForItemDto getLastBooking(long itemId) {
        Booking booking = bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (booking != null) {
            return bookingMapper.toBookingResponseForItemDto(booking);
        } else {
            return null;
        }
    }

    private List<CommentDto> getCommentByItemId(long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toCommentDto).collect(Collectors.toList());
    }
}
