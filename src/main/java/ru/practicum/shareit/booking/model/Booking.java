package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@ToString
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "booker_id")
    private User booker;


    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        return id != null && id.equals(((Booking) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
