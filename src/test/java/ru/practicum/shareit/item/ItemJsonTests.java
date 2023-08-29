package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingResponseForItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemJsonTests {
    @Autowired
    private JacksonTester<ItemDto> itemDtoJacksonTester;

    @Autowired
    private JacksonTester<ItemWithBookingAndCommentsDto> itemWithBookingAndCommentsDtoJacksonTester;

    @Test
    void itemDtoTest() throws Exception {
        ItemDto itemDto = new ItemDto(1, "Лодка", "Резиновая лодка", true, 2L);

        JsonContent<ItemDto> result = itemDtoJacksonTester.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) itemDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(Long.valueOf(itemDto.getRequestId()).intValue());
    }

    @Test
    void ItemWithBookingAndCommentsDtoTest() throws Exception {
        ItemWithBookingAndCommentsDto itemWithBookingAndCommentsDto = new ItemWithBookingAndCommentsDto();
        itemWithBookingAndCommentsDto.setId(1);
        itemWithBookingAndCommentsDto.setName("Лодка");
        itemWithBookingAndCommentsDto.setDescription("Резиновая лодка");
        itemWithBookingAndCommentsDto.setAvailable(true);
        itemWithBookingAndCommentsDto.setNextBooking(null);
        itemWithBookingAndCommentsDto.setLastBooking(new BookingResponseForItemDto(1, 3));
        itemWithBookingAndCommentsDto.setComments(List.of(
                new CommentDto(1, "Великолепно!", "Влад", LocalDateTime.of(2023, 2, 23, 17, 20))));

        JsonContent<ItemWithBookingAndCommentsDto> result = itemWithBookingAndCommentsDtoJacksonTester.write(itemWithBookingAndCommentsDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.comments");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) itemWithBookingAndCommentsDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemWithBookingAndCommentsDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemWithBookingAndCommentsDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemWithBookingAndCommentsDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo((int) itemWithBookingAndCommentsDto.getLastBooking().getId());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo((int) itemWithBookingAndCommentsDto.getLastBooking().getBookerId());
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo((int) itemWithBookingAndCommentsDto.getComments().get(0).getId());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo(itemWithBookingAndCommentsDto.getComments().get(0).getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo(itemWithBookingAndCommentsDto.getComments().get(0).getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo(itemWithBookingAndCommentsDto.getComments().get(0).getCreated().toString() + ":00");
    }
}
