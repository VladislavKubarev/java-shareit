package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingIntegrationTests {

    @Autowired
    private BookingServiceImpl bookingServiceImpl;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findBookingsByBookerTest() {
        User owner = createUser("Влад", "vlad@yandex.ru");
        User booker = createUser("Стас", "stas@yandex.ru");
        userRepository.saveAll(List.of(owner, booker));

        Item item1 = createItem("Лодка", "Резиновая", true, owner);
        Item item2 = createItem("Стол", "Деревянный", true, owner);
        Item item3 = createItem("Стакан", "Стеклянный", true, owner);
        Item item4 = createItem("Матрац", "Старый", true, owner);
        Item item5 = createItem("Дрель", "Новая", true, owner);
        itemRepository.saveAll(List.of(item1, item2, item3, item4, item5));

        Booking pastBooking = createPastBooking(item1, booker);
        Booking currentBooking = createCurrentBooking(item2, booker);
        Booking futureBooking = createFutureBooking(item3, booker);
        Booking rejectedBooking = createRejectedBooking(item5, booker);
        bookingRepository.saveAll(List.of(pastBooking, currentBooking, futureBooking, rejectedBooking));

        Collection<BookingResponseDto> all = bookingServiceImpl.findBookingsByBooker(booker.getId(), "ALL", 0, 10);
        Collection<BookingResponseDto> past = bookingServiceImpl.findBookingsByBooker(booker.getId(), "PAST", 0, 10);
        Collection<BookingResponseDto> current = bookingServiceImpl.findBookingsByBooker(booker.getId(), "CURRENT", 0, 10);
        Collection<BookingResponseDto> future = bookingServiceImpl.findBookingsByBooker(booker.getId(), "FUTURE", 0, 10);
        Collection<BookingResponseDto> waiting = bookingServiceImpl.findBookingsByBooker(booker.getId(), "WAITING", 0, 10);
        Collection<BookingResponseDto> rejected = bookingServiceImpl.findBookingsByBooker(booker.getId(), "REJECTED", 0, 10);

        assertThat(all.size()).isEqualTo(4);
        assertThat(past.size()).isEqualTo(1);
        assertThat(past).contains(BookingMapper.toBookingDto(pastBooking));
        assertThat(current.size()).isEqualTo(2);
        assertThat(current).contains(BookingMapper.toBookingDto(currentBooking));
        assertThat(current).contains(BookingMapper.toBookingDto(rejectedBooking));
        assertThat(future.size()).isEqualTo(1);
        assertThat(future).contains(BookingMapper.toBookingDto(futureBooking));
        assertThat(waiting.size()).isEqualTo(1);
        assertThat(waiting).contains(BookingMapper.toBookingDto(futureBooking));
        assertThat(rejected.size()).isEqualTo(1);
        assertThat(rejected).contains(BookingMapper.toBookingDto(rejectedBooking));
    }

    @Test
    void findBookingsByOwnerTest() {
        User owner = createUser("Влад", "vlad@yandex.ru");
        User booker = createUser("Стас", "stas@yandex.ru");
        userRepository.saveAll(List.of(owner, booker));

        Item item1 = createItem("Лодка", "Резиновая", true, owner);
        Item item2 = createItem("Стол", "Деревянный", true, owner);
        Item item3 = createItem("Стакан", "Стеклянный", true, owner);
        Item item4 = createItem("Матрац", "Старый", true, owner);
        Item item5 = createItem("Дрель", "Новая", true, owner);
        itemRepository.saveAll(List.of(item1, item2, item3, item4, item5));

        Booking pastBooking = createPastBooking(item1, booker);
        Booking currentBooking = createCurrentBooking(item2, booker);
        Booking futureBooking = createFutureBooking(item3, booker);
        Booking rejectedBooking = createRejectedBooking(item5, booker);
        bookingRepository.saveAll(List.of(pastBooking, currentBooking, futureBooking, rejectedBooking));

        Collection<BookingResponseDto> all = bookingServiceImpl.findBookingsByOwner(owner.getId(), "ALL", 0, 10);
        Collection<BookingResponseDto> past = bookingServiceImpl.findBookingsByOwner(owner.getId(), "PAST", 0, 10);
        Collection<BookingResponseDto> current = bookingServiceImpl.findBookingsByOwner(owner.getId(), "CURRENT", 0, 10);
        Collection<BookingResponseDto> future = bookingServiceImpl.findBookingsByOwner(owner.getId(), "FUTURE", 0, 10);
        Collection<BookingResponseDto> waiting = bookingServiceImpl.findBookingsByOwner(owner.getId(), "WAITING", 0, 10);
        Collection<BookingResponseDto> rejected = bookingServiceImpl.findBookingsByOwner(owner.getId(), "REJECTED", 0, 10);

        assertThat(all.size()).isEqualTo(4);
        assertThat(past.size()).isEqualTo(1);
        assertThat(past).contains(BookingMapper.toBookingDto(pastBooking));
        assertThat(current.size()).isEqualTo(2);
        assertThat(current).contains(BookingMapper.toBookingDto(currentBooking));
        assertThat(current).contains(BookingMapper.toBookingDto(rejectedBooking));
        assertThat(future.size()).isEqualTo(1);
        assertThat(future).contains(BookingMapper.toBookingDto(futureBooking));
        assertThat(waiting.size()).isEqualTo(1);
        assertThat(waiting).contains(BookingMapper.toBookingDto(futureBooking));
        assertThat(rejected.size()).isEqualTo(1);
        assertThat(rejected).contains(BookingMapper.toBookingDto(rejectedBooking));
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);

        return item;
    }

    private Booking createPastBooking(Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.of(2023, 8, 27, 10, 30));
        booking.setEnd(LocalDateTime.of(2023, 8, 28, 10, 30));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.CANCELED);

        return booking;
    }

    private Booking createCurrentBooking(Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.of(2023, 8, 28, 10, 30));
        booking.setEnd(LocalDateTime.of(2023, 8, 30, 10, 30));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        return booking;
    }

    private Booking createFutureBooking(Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.of(2023, 8, 30, 10, 30));
        booking.setEnd(LocalDateTime.of(2023, 8, 31, 10, 30));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        return booking;
    }

    private Booking createRejectedBooking(Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.of(2023, 8, 28, 10, 30));
        booking.setEnd(LocalDateTime.of(2023, 9, 1, 10, 30));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.REJECTED);

        return booking;
    }
}
