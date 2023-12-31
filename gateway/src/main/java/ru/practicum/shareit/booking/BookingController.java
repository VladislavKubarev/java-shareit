package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody BookingRequestDto bookingDto) {
        log.info("Получен POST-запрос на создание бронирования от пользователя с ID={}", userId);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId, @RequestParam Boolean approved) {
        log.info("Получен PATCH-запрос на подтверждения бронирования с ID={}", bookingId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long bookingId) {
        log.info("Получен GET-запрос на получение бронирования с ID={}", bookingId);
        return bookingClient.findBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findBookingsByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                                       @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        log.info("Получен GET-запрос на получение списка всех бронирований вещей пользователя с ID={} " +
                "и параметром STATE={}", userId, state);
        return bookingClient.findBookingsByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                                      @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        log.info("Получен GET-запрос на получение списка всех бронирований вещей пользователя с ID={} " +
                "и параметром STATE={}", ownerId, state);
        return bookingClient.findBookingsByOwner(ownerId, state, from, size);
    }
}
