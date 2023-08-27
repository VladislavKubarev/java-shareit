package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemResponseForBookingDto;
import ru.practicum.shareit.user.dto.UserResponseForBookingDto;

@Component
public class BookingMapper {
    public static BookingResponseDto toBookingDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new UserResponseForBookingDto(booking.getBooker().getId()),
                new ItemResponseForBookingDto(booking.getItem().getId(), booking.getItem().getName())
        );
    }

    public static Booking toBooking(BookingRequestDto bookingRequestDto) {
        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());

        return booking;
    }

    public static BookingResponseForItemDto toBookingResponseForItemDto(Booking booking) {
        return new BookingResponseForItemDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }
}
