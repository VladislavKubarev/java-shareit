package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(long userId, BookingRequestDto bookingDto);

    BookingResponseDto approveBooking(long userId, long bookingId, boolean approved);

    BookingResponseDto findBookingById(long userId, long bookingId);

    List<BookingResponseDto> findBookingsByBooker(long userId, String state, int from, int size);

    List<BookingResponseDto> findBookingsByOwner(long userId, String state, int from, int size);
}
