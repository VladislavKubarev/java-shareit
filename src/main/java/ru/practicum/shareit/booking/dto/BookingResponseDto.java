package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseForBookingDto;
import ru.practicum.shareit.user.dto.UserResponseForBookingDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private UserResponseForBookingDto booker;
    private ItemResponseForBookingDto item;
}
