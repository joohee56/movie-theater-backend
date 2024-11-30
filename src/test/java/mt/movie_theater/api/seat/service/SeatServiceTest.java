package mt.movie_theater.api.seat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import java.util.Map;
import mt.movie_theater.IntegrationTestSupport;
import mt.movie_theater.api.exception.DuplicateSeatBookingException;
import mt.movie_theater.api.seat.response.SeatResponse;
import mt.movie_theater.api.seat.response.SeatSummaryResponse;
import mt.movie_theater.domain.booking.Booking;
import mt.movie_theater.domain.booking.BookingRepository;
import mt.movie_theater.domain.booking.BookingStatus;
import mt.movie_theater.domain.bookingseat.BookingSeat;
import mt.movie_theater.domain.bookingseat.BookingSeatRepository;
import mt.movie_theater.domain.hall.Hall;
import mt.movie_theater.domain.hall.HallRepository;
import mt.movie_theater.domain.screening.Screening;
import mt.movie_theater.domain.screening.ScreeningRepository;
import mt.movie_theater.domain.seat.Seat;
import mt.movie_theater.domain.seat.SeatLocation;
import mt.movie_theater.domain.seat.SeatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class SeatServiceTest extends IntegrationTestSupport {
    @Autowired
    private SeatService seatService;
    @Autowired
    private HallRepository hallRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ScreeningRepository screeningRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    @Transactional
    @DisplayName("상영관에 좌석 리스트를 등록한다.")
    @Test
    void createSeatList() {
        //given
        Hall hall = createHall();
        int rows = 2;
        int columns = 3;

        //when
        List<SeatResponse> seats = seatService.createSeatList(hall.getId(), rows, columns);

        //then
        assertThat(seats).hasSize(6)
                .extracting("section", "seatRow")
                .containsExactlyInAnyOrder(
                        tuple("A", "1"),
                        tuple("A", "2"),
                        tuple("A", "3"),
                        tuple("B", "1"),
                        tuple("B", "2"),
                        tuple("B", "3")
                );
    }

    @DisplayName("상영관에 좌석 리스트를 등록할 때, 유효하지 않은 상영관일 경우 예외가 발생한다.")
    @Test
    void createSeatListWithNoHall() {
        //given
        int rows = 2;
        int columns = 3;

        //when, then
        assertThatThrownBy(() -> seatService.createSeatList(1L, rows, columns))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 상영관입니다. 상영관 정보를 다시 확인해 주세요.");
    }

    @DisplayName("상영관의 좌석 리스트를 섹션별로 좌석의 예매 유무와 함께 조회한다.")
    @Test
    void getSeatList() {
        //given
        Hall hall1 = createHall();
        Hall hall2 = createHall();
        Screening screening = createScreening(hall1);
        Seat seat1 = createSeat(hall1, "A", "1");
        Seat seat2 = createSeat(hall1, "A", "2");
        Seat seat3 = createSeat(hall1, "B", "1");
        Seat seat4 = createSeat(hall1, "B", "2");
        //상영관 불일치
        Seat seat5 = createSeat(hall2, "A", "1");

        Booking booking1 = createBooking(screening, BookingStatus.CONFIRMED);
        createBookingSeat(booking1, seat1);
        Booking booking2 = createBooking(screening, BookingStatus.CANCELED);
        createBookingSeat(booking2, seat2);

        //when
        Map<String, List<SeatSummaryResponse>> sectionSeatMap = seatService.getSeatList(screening.getId(), hall1.getId());

        //then
        assertThat(sectionSeatMap).hasSize(2);
        assertThat(sectionSeatMap.get("A")).hasSize(2)
                .extracting("section", "seatRow", "isBooked")
                .containsExactlyInAnyOrder(
                        tuple("A", "1", true),
                        tuple("A", "2", false)
                );
        assertThat(sectionSeatMap.get("B")).hasSize(2)
                .extracting("section", "seatRow", "isBooked")
                .containsExactlyInAnyOrder(
                        tuple("B", "1", false),
                        tuple("B", "2", false)
                );
    }

    @DisplayName("좌석 리스트를 검증한다. 유효한 좌석일 경우 좌석 리스트를 반환한다.")
    @Test
    void validateSeats() {
        //given
        Seat seat1 = createSeat("A", "1");
        Seat seat2 = createSeat("B", "1");
        List<Long> seatIds = List.of(seat1.getId(), seat2.getId());

        Screening screening = createScreening();

        //when
        List<Seat> seats = seatService.validateSeats(seatIds, screening);

        //then
        assertThat(seats).hasSize(2)
                .extracting("id", "seatLocation")
                .containsExactlyInAnyOrder(
                        tuple(seat1.getId(), new SeatLocation("A", "1")),
                        tuple(seat2.getId(), new SeatLocation("B", "1"))
                );
    }

    @DisplayName("좌석 리스트를 검증할 때, 유효하지 않은 좌석이 포함될 경우 예외가 발생한다.")
    @Test
    void validateSeatsWithNoSeat() {
        //given
        Seat seat1 = createSeat("A", "1");
        List<Long> seatIds = List.of(seat1.getId(), 2L);

        Screening screening = createScreening();

        //when, then
        assertThatThrownBy(() -> seatService.validateSeats(seatIds, screening))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 좌석입니다. 좌석 정보를 다시 확인해 주세요.");
    }

    @DisplayName("좌석 리스트를 검증할 때, 판매가 진행 중인 좌석이 포함될 경우 예외가 발생한다.")
    @Test
    void validateSeatsWithPendingSeat() {
        //given
        Seat seat1 = createSeat("A", "1");
        Seat seat2 = createSeat("B", "1");
        List<Long> seatIds = List.of(seat1.getId(), seat2.getId());

        Screening screening = createScreening();
        Booking booking = createBooking(screening, BookingStatus.PENDING);
        createBookingSeat(booking, seat1);

        //when, then
        assertThatThrownBy(() -> seatService.validateSeats(seatIds, screening))
                .isInstanceOf(DuplicateSeatBookingException.class)
                .hasMessage("판매가 진행중인 좌석입니다.");
    }

    @DisplayName("좌석 리스트를 검증할 때, 이미 선택된 좌석이 포함될 경우 예외가 발생한다.")
    @Test
    void validateSeatsWithConfirmedSeat() {
        //given
        Seat seat1 = createSeat("A", "1");
        Seat seat2 = createSeat("B", "1");
        List<Long> seatIds = List.of(seat1.getId(), seat2.getId());

        Screening screening = createScreening();
        Booking booking = createBooking(screening, BookingStatus.CONFIRMED);
        createBookingSeat(booking, seat1);

        //when, then
        assertThatThrownBy(() -> seatService.validateSeats(seatIds, screening))
                .isInstanceOf(DuplicateSeatBookingException.class)
                .hasMessage("이미 선택된 좌석입니다.");
    }

    @DisplayName("좌석 리스트를 검증할 때, 결제 취소된 좌석이 아닌 좌석이 포함될 경우 예외가 발생한다.")
    @Test
    void validateSeatsWithNoCanceledSeat() {
        //given
        Seat seat1 = createSeat("A", "1");
        Seat seat2 = createSeat("B", "1");
        List<Long> seatIds = List.of(seat1.getId(), seat2.getId());

        Screening screening = createScreening();
        Booking booking = createBooking(screening, BookingStatus.COMPLETED);
        createBookingSeat(booking, seat1);

        //when, then
        assertThatThrownBy(() -> seatService.validateSeats(seatIds, screening))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("불가능한 예매입니다.");
    }

    private Screening createScreening() {
        Screening screening = Screening.builder()
                .build();
        return screeningRepository.save(screening);
    }
    private Screening createScreening(Hall hall) {
        Screening screening = Screening.builder()
                .hall(hall)
                .build();
        return screeningRepository.save(screening);
    }
    private Hall createHall() {
        Hall hall = Hall.builder()
                .build();
        return hallRepository.save(hall);
    }
    private Seat createSeat(String section, String seatRow) {
        Seat seat = Seat.builder()
                .seatLocation(new SeatLocation(section, seatRow))
                .build();
        return seatRepository.save(seat);
    }
    private Seat createSeat(Hall hall, String section, String seatRow) {
        Seat seat = Seat.builder()
                .hall(hall)
                .seatLocation(new SeatLocation(section, seatRow))
                .build();
        return seatRepository.save(seat);
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