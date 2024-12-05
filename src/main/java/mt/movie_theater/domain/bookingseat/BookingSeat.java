package mt.movie_theater.domain.bookingseat;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mt.movie_theater.domain.BaseEntity;
import mt.movie_theater.domain.booking.Booking;
import mt.movie_theater.domain.seat.Seat;

@Entity
@NoArgsConstructor
@Getter
public class BookingSeat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    private Seat seat;

    @Builder
    private BookingSeat(Booking booking, Seat seat) {
        this.booking = booking;
        this.seat = seat;
    }

    public static BookingSeat create(Seat seat) {
        return BookingSeat.builder()
                .seat(seat)
                .build();
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
