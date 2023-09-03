package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findItemsByUser() {
        User owner = createUser("Влад", "vlad@yandex.ru");
        User anotherUser = createUser("Стас", "stas@yandex.ru");
        User commentAuthor = createUser("Вова", "vova@yandex.ru");
        userRepository.saveAll(List.of(owner, anotherUser, commentAuthor));

        Item item1 = createItem("Лодка", "Резиновая", true, owner);
        Item item2 = createItem("Стол", "Деревянный", true, anotherUser);
        Item item3 = createItem("Стакан", "Стеклянный", true, owner);
        itemRepository.saveAll(List.of(item1, item2, item3));

        Booking booking = createBooking(
                LocalDateTime.of(2023, 8, 29, 10, 30),
                LocalDateTime.of(2023, 8, 31, 10, 30),
                item1, anotherUser, BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Comment comment1ForItem3 = createComment("Отлично!", item3, commentAuthor, LocalDateTime.now());
        Comment comment2ForItem3 = createComment("Превосходно!", item3, commentAuthor, LocalDateTime.now());
        commentRepository.saveAll(List.of(comment1ForItem3, comment2ForItem3));

        List<ItemWithBookingAndCommentsDto> allItems = itemService.findItemsByUser(owner.getId(), 0, 10);

        assertThat(allItems.size()).isEqualTo(2);
        assertThat(allItems.get(0).getName()).isEqualTo("Лодка");
        assertThat(allItems.get(0).getLastBooking()).isNotNull();
        assertThat(allItems.get(1).getName()).isEqualTo("Стакан");
        assertThat(allItems.get(1).getComments().size()).isEqualTo(2);
        assertThat(allItems.get(1).getComments().get(0).getText()).isEqualTo("Отлично!");
        assertThat(allItems.get(1).getComments().get(1).getText()).isEqualTo("Превосходно!");
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

    private Booking createBooking(LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);

        return booking;
    }

    private Comment createComment(String text, Item item, User author, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);

        return comment;
    }
}
