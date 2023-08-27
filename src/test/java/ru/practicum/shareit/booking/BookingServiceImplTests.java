package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AvailableException;
import ru.practicum.shareit.exception.InvalidStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTests {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    BookingServiceImpl bookingServiceImpl;

    @Test
    void createValidBooking() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User booker = createUser(2, "Иван", "ivan@yandex.ru");

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner);

        Booking booking = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item, booker, BookingStatus.APPROVED);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(booking.getStart());
        bookingRequestDto.setEnd(booking.getEnd());

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto actualBooking = bookingServiceImpl.createBooking(booker.getId(), bookingRequestDto);

        assertThat(actualBooking.getId(), equalTo(booking.getId()));
        assertThat(actualBooking.getStart(), equalTo(booking.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(actualBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(actualBooking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(actualBooking.getItem().getId(), equalTo(item.getId()));

        verify(userRepository, times(1)).findById(booker.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createValidBookingWhenUserIsNotFoundTest() {
        long userId = 0;

        BookingRequestDto bookingRequestDto = new BookingRequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.createBooking(userId, bookingRequestDto));

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createValidBookingWhenItemIsNotFoundTest() {
        User user = createUser(1, "Владислав", "vlad@yandex.ru");
        long itemId = 0;

        BookingRequestDto bookingRequestDto = new BookingRequestDto();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.createBooking(user.getId(), bookingRequestDto));

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingWhenItemUnavailableTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User booker = createUser(2, "Иван", "ivan@yandex.ru");

        Item item = createItem(1, "Шкаф", "Большой шкаф", false, owner);

        Booking booking = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item, booker, BookingStatus.APPROVED);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(booking.getStart());
        bookingRequestDto.setEnd(booking.getEnd());

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        AvailableException exception = assertThrows(AvailableException.class,
                () -> bookingServiceImpl.createBooking(booker.getId(), bookingRequestDto));

        assertThat("Вещь недоступна для бронирования!", equalTo(exception.getMessage()));

        verify(userRepository, times(1)).findById(booker.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingByOwnerTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner);

        Booking booking = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item, owner, BookingStatus.APPROVED);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(booking.getStart());
        bookingRequestDto.setEnd(booking.getEnd());

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.createBooking(owner.getId(), bookingRequestDto));

        assertThat("Владелец не может забронировать свою вещь!", equalTo(exception.getMessage()));

        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void correctApproveBookingTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User booker = createUser(2, "Иван", "ivan@yandex.ru");

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner);

        Booking booking = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item, booker, BookingStatus.WAITING);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(booking.getStart());
        bookingRequestDto.setEnd(booking.getEnd());

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto actualBooking = bookingServiceImpl.approveBooking(owner.getId(), booking.getId(), true);

        assertThat(actualBooking.getId(), equalTo(booking.getId()));
        assertThat(actualBooking.getStart(), equalTo(booking.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(actualBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(actualBooking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(actualBooking.getItem().getId(), equalTo(item.getId()));

        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(bookingRepository, times(1)).save((any(Booking.class)));
    }

    @Test
    void approveBookingWhenIsNotOwnerTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User booker = createUser(2, "Иван", "ivan@yandex.ru");

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner);

        Booking booking = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item, booker, BookingStatus.WAITING);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(booking.getStart());
        bookingRequestDto.setEnd(booking.getEnd());

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.approveBooking(booker.getId(), booking.getId(), true));

        assertThat("Пользователь не является хозяином вещи!", equalTo(exception.getMessage()));

        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void approveBookingWhenBookingIsApprovedTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User booker = createUser(2, "Иван", "ivan@yandex.ru");

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner);

        Booking booking = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item, booker, BookingStatus.APPROVED);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(booking.getStart());
        bookingRequestDto.setEnd(booking.getEnd());

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        InvalidStateException exception = assertThrows(InvalidStateException.class,
                () -> bookingServiceImpl.approveBooking(owner.getId(), booking.getId(), true));

        assertThat("Бронирование уже подтверждено!", equalTo(exception.getMessage()));

        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void approveBookingWhenBookingIsNotFoundTest() {
        long bookingId = 0;
        long ownerId = 1;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.approveBooking(ownerId, bookingId, true));

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void findBookingByIdOwnerTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User booker = createUser(2, "Иван", "ivan@yandex.ru");

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner);

        Booking booking = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item, booker, BookingStatus.APPROVED);

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingResponseDto actualBooking = bookingServiceImpl.findBookingById(owner.getId(), booking.getId());

        assertThat(actualBooking.getId(), equalTo(booking.getId()));
        assertThat(actualBooking.getStart(), equalTo(booking.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(actualBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(actualBooking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(actualBooking.getItem().getId(), equalTo(item.getId()));

        verify(userRepository, times(1)).findById(owner.getId());
        verify(bookingRepository, times(1)).findById(booking.getId());
    }

    @Test
    void findBookingByIdOtherUserTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User booker = createUser(2, "Иван", "ivan@yandex.ru");
        User otherUser = createUser(3, "Статс", "stas@yandex.ru");

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner);

        Booking booking = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item, booker, BookingStatus.APPROVED);

        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.findBookingById(otherUser.getId(), booking.getId()));

        assertThat("Пользователь не является хозяином вещи или хозяином бронирования!", equalTo(exception.getMessage()));

        verify(userRepository, times(1)).findById(otherUser.getId());
        verify(bookingRepository, times(1)).findById(booking.getId());
    }

    @Test
    void findBookingByIdWhenBookingIsNotFoundTest() {
        long bookingId = 0;
        long ownerId = 1;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.findBookingById(ownerId, bookingId));

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void findBookingByIdWhenUserIsNotFoundTest() {
        long userId = 0;
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User booker = createUser(2, "Иван", "ivan@yandex.ru");

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner);

        Booking booking = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item, booker, BookingStatus.APPROVED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.findBookingById(userId, booking.getId()));

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findById(booking.getId());
    }

    @Test
    void findAllBookingsByBookerTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User booker = createUser(2, "Иван", "ivan@yandex.ru");

        Item item1 = createItem(1, "Шкаф", "Большой шкаф", true, owner);
        Item item2 = createItem(2, "Лодка", "Дырявая резиновая лодка", true, owner);

        Booking bookingForItem1 = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item1, booker, BookingStatus.APPROVED);
        Booking bookingForItem2 = createBooking(2, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(15), item2, booker, BookingStatus.APPROVED);

        List<Booking> bookingsList = List.of(bookingForItem1, bookingForItem2);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(eq(booker.getId()), any(Pageable.class))).thenReturn(bookingsList);
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingsList);
        when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingsList);
        when(bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingsList);
        when(bookingRepository.findByBookerIdAndStatus(eq(booker.getId()), eq(BookingStatus.WAITING), any(Pageable.class))).thenReturn(bookingsList);
        when(bookingRepository.findByBookerIdAndStatus(eq(booker.getId()), eq(BookingStatus.REJECTED), any(Pageable.class))).thenReturn(bookingsList);

        List<BookingResponseDto> bookingListStateAll = bookingServiceImpl.findBookingsByBooker(booker.getId(), "ALL", 0, 5);
        bookingServiceImpl.findBookingsByBooker(booker.getId(), "CURRENT", 0, 5);
        bookingServiceImpl.findBookingsByBooker(booker.getId(), "PAST", 0, 5);
        bookingServiceImpl.findBookingsByBooker(booker.getId(), "FUTURE", 0, 5);
        bookingServiceImpl.findBookingsByBooker(booker.getId(), "WAITING", 0, 5);
        bookingServiceImpl.findBookingsByBooker(booker.getId(), "REJECTED", 0, 5);

        assertThrows(InvalidStateException.class,
                () -> bookingServiceImpl.findBookingsByBooker(booker.getId(), "UNKNOWN", 0, 5));
        assertThat(bookingListStateAll.get(0).getId(), equalTo(bookingForItem1.getId()));
        assertThat(bookingListStateAll.get(1).getId(), equalTo(bookingForItem2.getId()));

        verify(userRepository, times(7)).findById(booker.getId());
    }

    @Test
    void findAllBookingsByOwnerTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User booker = createUser(2, "Иван", "ivan@yandex.ru");

        Item item1 = createItem(1, "Шкаф", "Большой шкаф", true, owner);
        Item item2 = createItem(2, "Лодка", "Дырявая резиновая лодка", true, owner);

        Booking bookingForItem1 = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item1, booker, BookingStatus.APPROVED);
        Booking bookingForItem2 = createBooking(2, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(15), item2, booker, BookingStatus.APPROVED);

        List<Booking> bookingsList = List.of(bookingForItem1, bookingForItem2);

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(eq(owner.getId()), any(Pageable.class))).thenReturn(bookingsList);
        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(eq(owner.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingsList);
        when(bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(eq(owner.getId()), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingsList);
        when(bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(eq(owner.getId()), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingsList);
        when(bookingRepository.findByItemOwnerIdAndStatus(eq(owner.getId()), eq(BookingStatus.WAITING), any(Pageable.class))).thenReturn(bookingsList);
        when(bookingRepository.findByItemOwnerIdAndStatus(eq(owner.getId()), eq(BookingStatus.REJECTED), any(Pageable.class))).thenReturn(bookingsList);

        List<BookingResponseDto> bookingListStateAll = bookingServiceImpl.findBookingsByOwner(owner.getId(), "ALL", 0, 5);
        bookingServiceImpl.findBookingsByOwner(owner.getId(), "CURRENT", 0, 5);
        bookingServiceImpl.findBookingsByOwner(owner.getId(), "PAST", 0, 5);
        bookingServiceImpl.findBookingsByOwner(owner.getId(), "FUTURE", 0, 5);
        bookingServiceImpl.findBookingsByOwner(owner.getId(), "WAITING", 0, 5);
        bookingServiceImpl.findBookingsByOwner(owner.getId(), "REJECTED", 0, 5);

        assertThrows(InvalidStateException.class,
                () -> bookingServiceImpl.findBookingsByOwner(owner.getId(), "UNKNOWN", 0, 5));
        assertThat(bookingListStateAll.get(0).getId(), equalTo(bookingForItem1.getId()));
        assertThat(bookingListStateAll.get(1).getId(), equalTo(bookingForItem2.getId()));

        verify(userRepository, times(7)).findById(owner.getId());
    }

    @Test
    void findAllBookingsByOwnerWhenOwnerIsNotFoundTest() {
        long ownerId = 0;

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.findBookingsByOwner(ownerId, "ALL", 0, 5));

        verify(userRepository, times(1)).findById(ownerId);
    }

    @Test
    void findAllBookingsByBookerWhenBookerIsNotFoundTest() {
        long bookerId = 0;

        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.findBookingsByBooker(bookerId, "ALL", 0, 5));

        verify(userRepository, times(1)).findById(bookerId);
    }

    private User createUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private Item createItem(long id, String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);

        return item;
    }

    private Booking createBooking(long id, LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);

        return booking;
    }
}
