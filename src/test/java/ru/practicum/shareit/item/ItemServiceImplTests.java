package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTests {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemServiceImpl;

    @Test
    void createValidItemWithItemRequestTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User requester = createUser(2, "Иван", "ivan@yandex.ru");

        ItemRequest itemRequest = createItemRequest(1, "Хочу большой шкаф", requester, LocalDateTime.now());

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(itemRequest.getId());

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto actualItem = itemServiceImpl.saveItem(owner.getId(), itemDto);

        assertThat(actualItem.getId(), equalTo(item.getId()));
        assertThat(actualItem.getName(), equalTo(item.getName()));
        assertThat(actualItem.getDescription(), equalTo(item.getDescription()));
        assertThat(actualItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(actualItem.getRequestId(), equalTo(itemRequest.getId()));

        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRequestRepository, times(1)).findById(itemRequest.getId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createValidItemWithoutItemRequestTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner, null);
        ItemDto itemDto = new ItemDto();

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto actualItem = itemServiceImpl.saveItem(owner.getId(), itemDto);

        assertThat(actualItem.getId(), equalTo(item.getId()));
        assertThat(actualItem.getName(), equalTo(item.getName()));
        assertThat(actualItem.getDescription(), equalTo(item.getDescription()));
        assertThat(actualItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(actualItem.getRequestId(), equalTo(null));

        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createValidItemWhenItemRequestIsNotFoundTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User requester = createUser(2, "Иван", "ivan@yandex.ru");

        ItemRequest itemRequest = createItemRequest(0, "Ищу резиновую лодку", requester, LocalDateTime.now());

        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(itemRequest.getId());

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemServiceImpl.saveItem(owner.getId(), itemDto));

        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRequestRepository, times(1)).findById(itemRequest.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void createValidItemWhenUserIsNotFoundTest() {
        User owner = createUser(0, "Владислав", "vlad@yandex.ru");

        ItemDto itemDto = new ItemDto();

        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemServiceImpl.saveItem(owner.getId(), itemDto));

        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateValidItemTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");

        Item oldItem = createItem(1, "Шкаф", "Большой шкаф", true, owner, null);
        Item newItem = createItem(1, "Шкаф-купе", "Вместительный шкаф шкаф", true, owner, null);
        ItemDto itemDto = new ItemDto();

        when(itemRepository.findById(oldItem.getId())).thenReturn(Optional.of(oldItem));
        when(itemRepository.save(any(Item.class))).thenReturn(newItem);

        ItemDto actualItem = itemServiceImpl.updateItem(itemDto, newItem.getId(), owner.getId());

        assertThat(actualItem.getId(), equalTo(newItem.getId()));
        assertThat(actualItem.getName(), equalTo(newItem.getName()));
        assertThat(actualItem.getDescription(), equalTo(newItem.getDescription()));
        assertThat(actualItem.getAvailable(), equalTo(newItem.getAvailable()));
        assertThat(actualItem.getRequestId(), equalTo(null));

        verify(itemRepository, times(1)).findById(oldItem.getId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateValidItemWhenUserIsNotOwnerTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User user = createUser(2, "Иван", "ivan@yandex.ru");

        Item oldItem = createItem(1, "Шкаф", "Большой шкаф", true, owner, null);
        Item newItem = createItem(1, "Шкаф-купе", "Вместительный шкаф шкаф", true, owner, null);
        ItemDto itemDto = new ItemDto();

        when(itemRepository.findById(oldItem.getId())).thenReturn(Optional.of(oldItem));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemServiceImpl.updateItem(itemDto, newItem.getId(), user.getId()));

        assertThat("Пользователь не является хозяином этой вещи!", equalTo(exception.getMessage()));

        verify(itemRepository, times(1)).findById(oldItem.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateValidItemWhenItemIsNotFoundTest() {
        long itemId = 0;
        long userId = 1;
        ItemDto itemDto = new ItemDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemServiceImpl.updateItem(itemDto, itemId, userId));

        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void findItemByIdWhenIsNotOwnerWithoutBookingsTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User user = createUser(2, "Иван", "ivan@yandex.ru");

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner, null);

        List<Comment> commentList = List.of(
                createComment(1, "Отлично!", item, user, LocalDateTime.now()));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(item.getId())).thenReturn(commentList);

        ItemWithBookingAndCommentsDto actualItem = itemServiceImpl.findItemById(item.getId(), user.getId());

        assertThat(actualItem.getId(), equalTo(item.getId()));
        assertThat(actualItem.getName(), equalTo(item.getName()));
        assertThat(actualItem.getDescription(), equalTo(item.getDescription()));
        assertThat(actualItem.getAvailable(), equalTo(item.getAvailable()));

        assertThat(actualItem.getComments().get(0).getId(), equalTo(commentList.get(0).getId()));
        assertThat(actualItem.getComments().get(0).getText(), equalTo(commentList.get(0).getText()));
        assertThat(actualItem.getComments().get(0).getAuthorName(), equalTo(commentList.get(0).getAuthor().getName()));
        assertThat(actualItem.getComments().get(0).getCreated(), equalTo(commentList.get(0).getCreated()));

        assertNull(actualItem.getLastBooking());
        assertNull(actualItem.getNextBooking());

        verify(itemRepository, times(1)).findById(item.getId());
        verify(commentRepository, times(1)).findByItemId(item.getId());
    }

    @Test
    void findItemByIdWhenIsOwnerWithBookingsTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User user = createUser(2, "Иван", "ivan@yandex.ru");

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner, null);

        List<Comment> commentList = List.of(
                createComment(1, "Отлично!", item, user, LocalDateTime.now()));

        Booking nextBooking = createBooking(
                2, LocalDateTime.now(), LocalDateTime.now().plusDays(3), item, user, BookingStatus.APPROVED);
        Booking lastBooking = createBooking(
                1, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, BookingStatus.APPROVED);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(
                eq(item.getId()), any(LocalDateTime.class), any(BookingStatus.class))).thenReturn(lastBooking);
        when(bookingRepository.findFirstByItemIdAndStartGreaterThanEqualAndStatusOrderByStartAsc(
                eq(item.getId()), any(LocalDateTime.class), any(BookingStatus.class))).thenReturn(nextBooking);
        when(commentRepository.findByItemId(item.getId())).thenReturn(commentList);

        ItemWithBookingAndCommentsDto actualItem = itemServiceImpl.findItemById(item.getId(), owner.getId());

        assertThat(actualItem.getId(), equalTo(item.getId()));
        assertThat(actualItem.getName(), equalTo(item.getName()));
        assertThat(actualItem.getDescription(), equalTo(item.getDescription()));
        assertThat(actualItem.getAvailable(), equalTo(item.getAvailable()));

        assertThat(actualItem.getComments().get(0).getId(), equalTo(commentList.get(0).getId()));
        assertThat(actualItem.getComments().get(0).getText(), equalTo(commentList.get(0).getText()));
        assertThat(actualItem.getComments().get(0).getAuthorName(), equalTo(commentList.get(0).getAuthor().getName()));
        assertThat(actualItem.getComments().get(0).getCreated(), equalTo(commentList.get(0).getCreated()));

        assertThat(actualItem.getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(actualItem.getLastBooking().getBookerId(), equalTo(lastBooking.getBooker().getId()));
        assertThat(actualItem.getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(actualItem.getNextBooking().getBookerId(), equalTo(nextBooking.getBooker().getId()));

        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, times(1))
                .findFirstByItemIdAndStartGreaterThanEqualAndStatusOrderByStartAsc(eq(item.getId()), any(LocalDateTime.class), any(BookingStatus.class));
        verify(bookingRepository, times(1))
                .findFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(eq(item.getId()), any(LocalDateTime.class), any(BookingStatus.class));
        verify(commentRepository, times(1)).findByItemId(item.getId());
    }

    @Test
    void findItemByIdWhenItemIsNotFoundTest() {
        long itemId = 0;
        long userId = 1;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemServiceImpl.findItemById(itemId, userId));

        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void findAllItemsByUserTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User user1 = createUser(2, "Иван", "ivan@yandex.ru");
        User user2 = createUser(3, "Стас", "stas@yandex.ru");

        List<Item> itemsList = List.of(
                createItem(1, "Шкаф", "Большой шкаф", true, owner, null),
                createItem(2, "Диван", "Старый диван", true, owner, null));

        Booking nextBooking = createBooking(
                2, LocalDateTime.now(), LocalDateTime.now().plusDays(3), itemsList.get(0), user1, BookingStatus.APPROVED);
        Booking lastBooking = createBooking(
                1, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5), itemsList.get(0), user2, BookingStatus.APPROVED);

        List<Booking> itemBookingList = List.of(nextBooking, lastBooking);

        List<Comment> commentList = List.of(
                createComment(1, "Отлично!", itemsList.get(0), user1, LocalDateTime.now()),
                createComment(2, "Не понравился!", itemsList.get(0), user2, LocalDateTime.now()));

        when(itemRepository.findByOwnerIdOrderByIdAsc(eq(owner.getId()), any(Pageable.class))).thenReturn(itemsList);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(eq(owner.getId()), any(Pageable.class))).thenReturn(itemBookingList);
        when(commentRepository.findByItemIn(itemsList)).thenReturn(commentList);

        List<ItemWithBookingAndCommentsDto> actualItems = itemServiceImpl.findItemsByUser(owner.getId(), 0, 5);

        assertThat(actualItems.get(0).getId(), equalTo(itemsList.get(0).getId()));
        assertThat(actualItems.get(0).getName(), equalTo(itemsList.get(0).getName()));
        assertThat(actualItems.get(0).getDescription(), equalTo(itemsList.get(0).getDescription()));
        assertThat(actualItems.get(0).getAvailable(), equalTo(itemsList.get(0).getAvailable()));
        assertThat(actualItems.get(1).getId(), equalTo(itemsList.get(1).getId()));
        assertThat(actualItems.get(1).getName(), equalTo(itemsList.get(1).getName()));
        assertThat(actualItems.get(1).getDescription(), equalTo(itemsList.get(1).getDescription()));
        assertThat(actualItems.get(1).getAvailable(), equalTo(itemsList.get(1).getAvailable()));
        assertThat(actualItems.size(), equalTo(2));

        assertThat(actualItems.get(0).getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(actualItems.get(0).getLastBooking().getBookerId(), equalTo(user2.getId()));
        assertThat(actualItems.get(0).getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(actualItems.get(0).getNextBooking().getBookerId(), equalTo(user1.getId()));

        assertNull(actualItems.get(1).getNextBooking());
        assertNull(actualItems.get(1).getLastBooking());

        assertThat(actualItems.get(0).getComments().get(0).getId(), equalTo(commentList.get(0).getId()));
        assertThat(actualItems.get(0).getComments().get(0).getText(), equalTo(commentList.get(0).getText()));
        assertThat(actualItems.get(0).getComments().get(0).getAuthorName(), equalTo(commentList.get(0).getAuthor().getName()));
        assertThat(actualItems.get(0).getComments().get(1).getId(), equalTo(commentList.get(1).getId()));
        assertThat(actualItems.get(0).getComments().get(1).getText(), equalTo(commentList.get(1).getText()));
        assertThat(actualItems.get(0).getComments().get(1).getAuthorName(), equalTo(commentList.get(1).getAuthor().getName()));

        assertNull(actualItems.get(1).getComments());

        verify(itemRepository, times(1)).findByOwnerIdOrderByIdAsc(eq(owner.getId()), any(Pageable.class));
        verify(bookingRepository, times(1)).findByItemOwnerIdOrderByStartDesc(eq(owner.getId()), any(Pageable.class));
        verify(commentRepository, times(1)).findByItemIn(itemsList);
    }

    @Test
    void searchItemTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        List<Item> itemsList = List.of(
                createItem(1, "Шкаф", "Большой шкаф", true, owner, null));

        String text = "ШКАФ";

        when(itemRepository.searchItems(eq(text), any(Pageable.class))).thenReturn(itemsList);

        List<ItemDto> actualItems = itemServiceImpl.searchItems(text, 0, 5);

        assertThat(actualItems.get(0).getId(), equalTo(itemsList.get(0).getId()));
        assertThat(actualItems.get(0).getName(), equalTo(itemsList.get(0).getName()));
        assertThat(actualItems.get(0).getDescription(), equalTo(itemsList.get(0).getDescription()));
        assertThat(actualItems.get(0).getAvailable(), equalTo(itemsList.get(0).getAvailable()));

        verify(itemRepository, times(1)).searchItems(eq(text), any(Pageable.class));
    }

    @Test
    void createValidCommentTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User user1 = createUser(2, "Иван", "ivan@yandex.ru");

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner, null);

        Comment comment = createComment(1, "Отлично!", item, user1, LocalDateTime.now());
        CommentDto commentDto = new CommentDto();

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerIdAndEndIsBeforeAndStatus(
                eq(item.getId()), eq(user1.getId()), any(LocalDateTime.class), any(BookingStatus.class))).thenReturn(List.of(new Booking()));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto actualComment = itemServiceImpl.addComment(user1.getId(), commentDto, item.getId());

        assertThat(actualComment.getId(), equalTo(comment.getId()));
        assertThat(actualComment.getText(), equalTo(comment.getText()));
        assertThat(actualComment.getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertThat(actualComment.getCreated(), equalTo(comment.getCreated()));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, times(1)).findByItemIdAndBookerIdAndEndIsBeforeAndStatus(
                eq(item.getId()), eq(user1.getId()), any(LocalDateTime.class), any(BookingStatus.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createCommentWhenUserWasNotBookerTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User user = createUser(2, "Иван", "ivan@yandex.ru");

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner, null);

        CommentDto commentDto = new CommentDto();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerIdAndEndIsBeforeAndStatus(
                eq(item.getId()), eq(user.getId()), any(LocalDateTime.class), any(BookingStatus.class))).thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class,
                () -> itemServiceImpl.addComment(user.getId(), commentDto, item.getId()));

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, times(1)).findByItemIdAndBookerIdAndEndIsBeforeAndStatus(
                eq(item.getId()), eq(user.getId()), any(LocalDateTime.class), any(BookingStatus.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createCommentWhenUserIsNotFoundTest() {
        long userId = 0;
        long itemId = 1;

        CommentDto commentDto = new CommentDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemServiceImpl.addComment(userId, commentDto, itemId));

        verify(userRepository, times(1)).findById(userId);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createCommentWhenItemIsNotFoundTest() {
        User user = createUser(2, "Иван", "ivan@yandex.ru");
        long itemId = 0;

        CommentDto commentDto = new CommentDto();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemServiceImpl.addComment(user.getId(), commentDto, itemId));

        verify(userRepository, times(1)).findById(user.getId());
        verify(commentRepository, never()).save(any(Comment.class));
    }


    private User createUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private Item createItem(long id, String name, String description, Boolean available, User owner, ItemRequest itemRequest) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(itemRequest);

        return item;
    }

    private ItemRequest createItemRequest(long id, String description, User requester, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequester(requester);
        itemRequest.setCreated(created);

        return itemRequest;
    }

    private Comment createComment(long id, String text, Item item, User author, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);

        return comment;
    }

    private Booking createBooking(long id, LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        return booking;
    }
}
