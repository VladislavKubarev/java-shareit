package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long bookerId, LocalDateTime start,
                                                                              LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long ownerId, LocalDateTime start,
                                                                                 LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(long ownerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(long ownerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatus(long ownerId, BookingStatus status, Pageable pageable);

    Booking findFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(long itemId, LocalDateTime start,
                                                                            BookingStatus status);

    Booking findFirstByItemIdAndStartGreaterThanEqualAndStatusOrderByStartAsc(long itemId, LocalDateTime start,
                                                                              BookingStatus status);

    List<Booking> findByItemIdAndBookerIdAndEndIsBeforeAndStatus(long itemId, long bookerId, LocalDateTime end,
                                                                 BookingStatus status);
}
