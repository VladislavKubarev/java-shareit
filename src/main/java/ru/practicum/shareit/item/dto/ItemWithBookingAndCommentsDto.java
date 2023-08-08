package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingResponseForItemDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithBookingAndCommentsDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingResponseForItemDto nextBooking;
    private BookingResponseForItemDto lastBooking;
    private List<CommentDto> comments;
}
