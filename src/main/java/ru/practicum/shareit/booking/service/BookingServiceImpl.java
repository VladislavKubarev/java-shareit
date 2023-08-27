package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AvailableException;
import ru.practicum.shareit.exception.InvalidStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto createBooking(long userId, BookingRequestDto bookingRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден!"));

        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(
                () -> new NotFoundException("Вещь не найдена!"));

        if (!item.getAvailable()) {
            throw new AvailableException("Вещь недоступна для бронирования!");
        }

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Владелец не может забронировать свою вещь!");
        }

        validationDate(bookingRequestDto);

        Booking booking = BookingMapper.toBooking(bookingRequestDto);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto approveBooking(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование не найдено!"));

        if (booking.getItem().getOwner().getId() == userId) {
            if (approved) {
                if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                    throw new InvalidStateException("Бронирование уже подтверждено!");
                } else {
                    booking.setStatus(BookingStatus.APPROVED);
                }
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new NotFoundException("Пользователь не является хозяином вещи!");
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto findBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование не найдено!"));

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден!"));

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("Пользователь не является хозяином вещи или хозяином бронирования!");
        }
    }

    @Override
    public List<BookingResponseDto> findBookingsByBooker(long bookerId, String state, int from, int size) {
        User user = userRepository.findById(bookerId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден!"));

        Pageable pageable = PageRequest.of(from / size, size);

        switch (state.toUpperCase()) {
            case "ALL":
                return bookingRepository.findByBookerIdOrderByStartDesc(bookerId, pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(bookerId,
                                LocalDateTime.now(), LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(bookerId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.WAITING, pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default:
                throw new InvalidStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingResponseDto> findBookingsByOwner(long ownerId, String state, int from, int size) {
        User user = userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден!"));

        Pageable pageable = PageRequest.of(from / size, size);

        switch (state.toUpperCase()) {
            case "ALL":
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId,
                                LocalDateTime.now(), LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default:
                throw new InvalidStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void validationDate(BookingRequestDto bookingRequestDto) {
        LocalDateTime start = bookingRequestDto.getStart();
        LocalDateTime end = bookingRequestDto.getEnd();
        if (start.isEqual(end) || end.isBefore(start)) {
            throw new ValidationException("Дата/время окончания бронирования не должны равняться " +
                    "или быть раньше даты/времени начала бронирования");
        }
    }
}
