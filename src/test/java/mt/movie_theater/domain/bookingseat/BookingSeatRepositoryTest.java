package mt.movie_theater.domain.bookingseat;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import java.util.List;
import mt.movie_theater.IntegrationTestSupport;
import mt.movie_theater.domain.booking.Booking;
import mt.movie_theater.domain.booking.BookingRepository;
import mt.movie_theater.domain.booking.BookingStatus;
import mt.movie_theater.domain.screening.Screening;
import mt.movie_theater.domain.screening.ScreeningRepository;
import mt.movie_theater.domain.seat.Seat;
import mt.movie_theater.domain.seat.SeatRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
class BookingSeatRepositoryTest extends IntegrationTestSupport {
    @Autowired
    private BookingSeatRepository bookingSeatRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ScreeningRepository screeningRepository;

    @DisplayName("상영시간과 좌석리스트에 해당하는 예매좌석 리스트를 조회한다.")
    @Test
    void findAllBySeatIdInAndScreening() {
        //given
        Screening screening1 = createScreening();
        Screening screening2 = createScreening();
        Booking booking1 = createBooking(screening1);
        Booking booking2 = createBooking(screening2);

        Seat seat1 = createSeat();
        Seat seat2 = createSeat();
        Seat seat3 = createSeat();
        Seat seat4 = createSeat();
        createBookingSeat(booking1, seat1);
        createBookingSeat(booking1, seat2);
        createBookingSeat(booking1, seat3); //좌석 리스트 미포함
        createBookingSeat(booking2, seat4); //상영시간 불일치

        List<Long> seatIds = List.of(seat1.getId(), seat2.getId());

        //when
        List<BookingSeat> bookingSeats = bookingSeatRepository.findAllBySeatIdInAndScreening(screening1, seatIds);

        //then
        assertThat(bookingSeats).hasSize(2)
                .extracting("booking", "seat")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(booking1, seat1),
                        Tuple.tuple(booking1, seat2)
                );
    }

    @DisplayName("조건에 해당하는 예매좌석의 좌석리스트를 조회한다. 조건은 상영시간, 예매상태가 주어진다.")
    @Test
    void findAllByScreeningIdAndBookingStatusNot() {
        //given
        Screening screening = createScreening();
        Seat seat1 = createSeat();
        Seat seat2 = createSeat();
        Seat seat3 = createSeat();

        BookingStatus canceled = BookingStatus.CANCELED;
        BookingStatus pending = BookingStatus.PENDING;
        BookingStatus confirmed = BookingStatus.CONFIRMED;

        Booking booking1 = createBooking(screening, canceled);
        createBookingSeat(booking1, seat1);
        Booking booking2 = createBooking(screening, pending);
        createBookingSeat(booking2, seat2);
        Booking booking3 = createBooking(screening, confirmed);
        createBookingSeat(booking3, seat3);

        //when
        List<Seat> seats = bookingSeatRepository.findAllByScreeningIdAndBookingStatusNot(screening.getId(), canceled);

        //then
        assertThat(seats).hasSize(2)
                .containsExactlyInAnyOrder(seat2, seat3);
    }

    private Screening createScreening() {
        Screening screening = Screening.builder().build();
        return screeningRepository.save(screening);
    }
    private Seat createSeat() {
        Seat seat = Seat.builder().build();
        return seatRepository.save(seat);
    }
    private Booking createBooking(Screening screening) {
        Booking booking = Booking.builder()
                .screening(screening)
                .build();
        return bookingRepository.save(booking);
    }
    private Booking createBooking(Screening screening, BookingStatus bookingStatus) {
        Booking booking = Booking.builder()
                .screening(screening)
                .bookingStatus(bookingStatus)
                .build();
        return bookingRepository.save(booking);
    }
    private BookingSeat createBookingSeat(Booking booking, Seat seat) {
        BookingSeat bookingSeat = BookingSeat.builder()
                .booking(booking)
                .seat(seat)
                .build();
        return bookingSeatRepository.save(bookingSeat);
    }
}