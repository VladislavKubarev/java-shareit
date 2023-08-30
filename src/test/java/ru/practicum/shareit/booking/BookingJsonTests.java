package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseForBookingDto;
import ru.practicum.shareit.user.dto.UserResponseForBookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingJsonTests {
    @Autowired
    private JacksonTester<BookingRequestDto> requestDtoJacksonTester;
    @Autowired
    private JacksonTester<BookingResponseDto> responseDtoJacksonTester;

    @Test
    void bookingRequestDtoTest() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                LocalDateTime.of(2023, 2, 23, 17, 20),
                LocalDateTime.of(2023, 2, 25, 10, 10), 1);

        JsonContent<BookingRequestDto> result = requestDtoJacksonTester.write(bookingRequestDto);

        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.itemId");

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingRequestDto.getStart() + ":00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingRequestDto.getEnd() + ":00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo((int) bookingRequestDto.getItemId());
    }

    @Test
    void bookingResponseDtoTest() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(1);
        bookingResponseDto.setStart(LocalDateTime.of(2023, 2, 23, 17, 20));
        bookingResponseDto.setEnd(LocalDateTime.of(2023, 2, 25, 10, 10));
        bookingResponseDto.setStatus(BookingStatus.APPROVED);
        bookingResponseDto.setBooker(new UserResponseForBookingDto(1));
        bookingResponseDto.setItem(new ItemResponseForBookingDto(1, "Лодка"));

        JsonContent<BookingResponseDto> result = responseDtoJacksonTester.write(bookingResponseDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.item");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) bookingResponseDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingResponseDto.getStart() + ":00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingResponseDto.getEnd() + ":00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingResponseDto.getStatus().toString());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo((int) bookingResponseDto.getBooker().getId());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo((int) bookingResponseDto.getItem().getId());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(bookingResponseDto.getItem().getName());
    }
}
