package mt.movie_theater.domain.booking;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mt.movie_theater.domain.BaseEntity;
import mt.movie_theater.domain.bookingseat.BookingSeat;
import mt.movie_theater.domain.payment.PaymentHistory;
import mt.movie_theater.domain.screening.Screening;
import mt.movie_theater.domain.seat.Seat;
import mt.movie_theater.domain.user.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Screening screening;

    @OneToOne(fetch = FetchType.LAZY)
    private PaymentHistory paymentHistory;

    private String bookingNumber;

    private LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> bookingSeats = new ArrayList<>();

    @Builder
    private Booking(User user, Screening screening, PaymentHistory paymentHistory, String bookingNumber,
                   LocalDateTime bookingTime, BookingStatus bookingStatus) {
        this.user = user;
        this.screening = screening;
        this.paymentHistory = paymentHistory;
        this.bookingNumber = bookingNumber;
        this.bookingTime = bookingTime;
        this.bookingStatus = bookingStatus;
    }

    public void addBookingSeat(BookingSeat bookingSeat) {
        this.bookingSeats.add(bookingSeat);
        bookingSeat.setBooking(this);
    }

    public void initPaymentHistory(PaymentHistory paymentHistory) {
        this.paymentHistory = paymentHistory;
    }

    public static Booking hold(User user, Screening screening, List<Seat> seats) {
        Booking booking = Booking.builder()
                .user(user)
                .screening(screening)
                .bookingStatus(BookingStatus.PENDING)
                .build();
        for (Seat seat : seats) {
            BookingSeat bookingSeat = BookingSeat.create(seat);
            booking.addBookingSeat(bookingSeat);
        }
        return booking;
    }

    public void confirm(String bookingNumber, LocalDateTime bookingTime) {
        this.bookingNumber = bookingNumber;
        this.bookingTime = bookingTime;
        this.bookingStatus = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        this.bookingStatus = BookingStatus.CANCELED;
    }
}
