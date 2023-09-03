package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {
    @Future(message = "Дата/время начала бронирования не может быть в прошлом!")
    @NotNull(message = "Дата/время начала бронирования не может быть пустой!")
    private LocalDateTime start;
    @Future(message = "Дата/время окончания бронирования не может быть в прошлом!")
    @NotNull(message = "Дата/время окончания бронирования не может быть пустой!")
    private LocalDateTime end;
    @NotNull(message = "ID вещи не может быть пустым!")
    private long itemId;

    @AssertTrue(message = "Дата/время окончания бронирования должно быть после даты/времени начала бронирования!")
    private boolean isEndAfterStart() {
        return start == null || end == null || end.isAfter(start);
    }

    @AssertTrue(message = "Дата/время окончания бронирования не может равняться дате/времени начала бронирования!")
    public boolean isEndEqualsStart() {
        return end == null || !end.equals(start);
    }
}
