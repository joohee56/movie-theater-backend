package mt.movie_theater.api.booking.service;

import static mt.movie_theater.domain.booking.BookingStatus.CANCELED;
import static mt.movie_theater.domain.booking.BookingStatus.COMPLETED;
import static mt.movie_theater.domain.booking.BookingStatus.CONFIRMED;
import static mt.movie_theater.domain.booking.BookingStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import mt.movie_theater.IntegrationTestSupport;
import mt.movie_theater.api.booking.request.BookingHoldRequest;
import mt.movie_theater.api.booking.response.BookingResponse;
import mt.movie_theater.api.booking.response.BookingWithDateResponse;
import mt.movie_theater.api.exception.DuplicateSeatBookingException;
import mt.movie_theater.api.payment.request.ConfirmBookingRequest;
import mt.movie_theater.api.payment.service.PaymentHistoryService;
import mt.movie_theater.domain.booking.Booking;
import mt.movie_theater.domain.booking.BookingRepository;
import mt.movie_theater.domain.booking.BookingStatus;
import mt.movie_theater.domain.bookingseat.BookingSeat;
import mt.movie_theater.domain.bookingseat.BookingSeatRepository;
import mt.movie_theater.domain.hall.Hall;
import mt.movie_theater.domain.hall.HallRepository;
import mt.movie_theater.domain.movie.Movie;
import mt.movie_theater.domain.movie.MovieRepository;
import mt.movie_theater.domain.movie.ScreeningType;
import mt.movie_theater.domain.payment.Currency;
import mt.movie_theater.domain.payment.PayStatus;
import mt.movie_theater.domain.payment.PaymentHistory;
import mt.movie_theater.domain.payment.PaymentHistoryRepository;
import mt.movie_theater.domain.screening.Screening;
import mt.movie_theater.domain.screening.ScreeningRepository;
import mt.movie_theater.domain.seat.Seat;
import mt.movie_theater.domain.seat.SeatLocation;
import mt.movie_theater.domain.seat.SeatRepository;
import mt.movie_theater.domain.theater.Theater;
import mt.movie_theater.domain.theater.TheaterRepository;
import mt.movie_theater.domain.user.User;
import mt.movie_theater.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class BookingServiceTest extends IntegrationTestSupport {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private ScreeningRepository screeningRepository;
    @Autowired
    private HallRepository hallRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;
    @Autowired
    private BookingSeatRepository bookingSeatRepository;
    @SpyBean
    private PaymentHistoryService paymentHistoryService;

    @DisplayName("예매를 사전등록한다.")
    @Test
    void holdBooking() {
        //given
        User user = createUser();
        LocalDateTime startDate = LocalDateTime.of(2024, 11, 29, 10, 0);
        Screening screening = createScreening(startDate);
        Seat seat1 = createSeat("A", "1");
        Seat seat2 = createSeat("B", "1");
        List<Seat> seats = List.of(seat1, seat2);

        BookingHoldRequest request = BookingHoldRequest.builder()
                .screeningId(screening.getId())
                .seatIds(seats.stream().map(Seat::getId).collect(Collectors.toList()))
                .build();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 28, 10, 0);

        //when
        Long bookingId = bookingService.holdBooking(user.getId(), request, bookingTime);

        //then
        Optional<Booking> optionalBooking = bookingRepository.findByIdWithBookingSeats(bookingId);
        assertThat(optionalBooking).isPresent();
        Booking booking = optionalBooking.get();
        assertThat(booking)
                .extracting("user", "screening", "bookingStatus")
                .containsExactly(user, screening, PENDING);
        assertThat(booking.getBookingSeats()).hasSize(2)
                .extracting("seat")
                .containsExactlyInAnyOrder(seat1, seat2);
    }

    @DisplayName("예매를 사전등록할 때, 유효하지 않은 사용자일 경우 예외가 발생한다.")
    @Test
    void holdBookingNoUser() {
        //given
        LocalDateTime startDate = LocalDateTime.of(2024, 11, 29, 10, 0);
        Screening screening = createScreening(startDate);
        Seat seat1 = createSeat("A", "1");
        Seat seat2 = createSeat("B", "1");
        List<Seat> seats = List.of(seat1, seat2);

        BookingHoldRequest request = BookingHoldRequest.builder()
                .screeningId(screening.getId())
                .seatIds(seats.stream().map(Seat::getId).collect(Collectors.toList()))
                .build();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 28, 10, 0);

        //when,then
        assertThatThrownBy(() -> bookingService.holdBooking(1L, request, bookingTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 사용자입니다. 사용자 정보를 다시 확인해 주세요.");
    }

    @DisplayName("예매를 사전등록할 때, 유효하지 않은 상영시간일 경우 예외가 발생한다.")
    @Test
    void holdBookingNoScreening() {
        //given
        User user = createUser();
        LocalDateTime startDate = LocalDateTime.of(2024, 11, 29, 10, 0);
        Seat seat1 = createSeat("A", "1");
        Seat seat2 = createSeat("B", "1");
        List<Seat> seats = List.of(seat1, seat2);

        BookingHoldRequest request = BookingHoldRequest.builder()
                .screeningId(1L)
                .seatIds(seats.stream().map(Seat::getId).collect(Collectors.toList()))
                .build();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 28, 10, 0);

        //when,then
        assertThatThrownBy(() -> bookingService.holdBooking(user.getId(), request, bookingTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 상영시간입니다. 상영시간 정보를 다시 확인해 주세요.");
    }

    @DisplayName("예매를 사전등록할 때, 예매시간이 상영 시작 시각 이후일 경우 예외가 발생한다.")
    @Test
    void holdBookingAfterScreeningStartDate() {
        //given
        User user = createUser();
        LocalDateTime startDate = LocalDateTime.of(2024, 11, 29, 10, 0);
        Screening screening = createScreening(startDate);
        Seat seat1 = createSeat("A", "1");
        Seat seat2 = createSeat("B", "1");
        List<Seat> seats = List.of(seat1, seat2);

        BookingHoldRequest request = BookingHoldRequest.builder()
                .screeningId(screening.getId())
                .seatIds(seats.stream().map(Seat::getId).collect(Collectors.toList()))
                .build();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 30, 10, 0);

        //when, then
        assertThatThrownBy(() -> bookingService.holdBooking(user.getId(), request, bookingTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상영 시작 시간이 지났습니다. 다른 상영 시간을 선택해 주세요.");
    }

    @DisplayName("예매를 사전등록할 때, 유효하지 않은 좌석이 포함될 경우 예외가 발생한다.")
    @Test
    void holdBookingNoSeat() {
        //given
        User user = createUser();
        LocalDateTime startDate = LocalDateTime.of(2024, 11, 29, 10, 0);
        Screening screening = createScreening(startDate);
        Seat seat = createSeat("A", "1");

        BookingHoldRequest request = BookingHoldRequest.builder()
                .screeningId(screening.getId())
                .seatIds(List.of(seat.getId(), 2L))
                .build();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 28, 10, 0);

        //when, then
        assertThatThrownBy(() -> bookingService.holdBooking(user.getId(), request, bookingTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 좌석입니다. 좌석 정보를 다시 확인해 주세요.");
    }

    @DisplayName("예매를 사전등록할 때, 판매 대기중인 좌석일 경우 예외가 발생한다.")
    @Test
    void holdBookingWithPendingSeat() {
        //given
        User user = createUser();
        LocalDateTime startDate = LocalDateTime.of(2024, 11, 29, 10, 0);
        Screening screening = createScreening(startDate);
        Seat seat1 = createSeat("A", "1");
        Seat seat2 = createSeat("B", "1");

        List<Seat> seats = List.of(seat1, seat2);
        BookingHoldRequest request = BookingHoldRequest.builder()
                .screeningId(screening.getId())
                .seatIds(seats.stream().map(Seat::getId).collect(Collectors.toList()))
                .build();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 28, 10, 0);
        //판매 대기 중
        bookingService.holdBooking(user.getId(), request, bookingTime);

        //when, then
        assertThatThrownBy(() -> bookingService.holdBooking(user.getId(), request, bookingTime))
                .isInstanceOf(DuplicateSeatBookingException.class)
                .hasMessage("판매가 진행중인 좌석입니다.");
    }

    @DisplayName("예매를 사전등록할 때, 이미 선택된 좌석일 경우 예외가 발생한다.")
    @Test
    void holdBookingWithConfirmedSeat() {
        //given
        User user = createUser();
        LocalDateTime startDate = LocalDateTime.of(2024, 11, 29, 10, 0);
        Screening screening = createScreening(startDate);
        Seat seat = createSeat("A", "1");
        Booking booking = createBooking(screening, CONFIRMED);
        createBookingSeat(booking, seat);

        List<Seat> seats = List.of(seat);
        BookingHoldRequest request = BookingHoldRequest.builder()
                .screeningId(screening.getId())
                .seatIds(seats.stream().map(Seat::getId).collect(Collectors.toList()))
                .build();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 28, 10, 0);

        //when, then
        assertThatThrownBy(() -> bookingService.holdBooking(user.getId(), request, bookingTime))
                .isInstanceOf(DuplicateSeatBookingException.class)
                .hasMessage("이미 선택된 좌석입니다.");
    }

    @DisplayName("예매를 사전등록할 때, 조회된 예매가 취소된 예매가 아닌 경우 예외가 발생한다.")
    @Test
    void holdBookingNoCanceled() {
        //given
        User user = createUser();
        LocalDateTime startDate = LocalDateTime.of(2024, 11, 29, 10, 0);
        Screening screening = createScreening(startDate);
        Seat seat = createSeat("A", "1");
        Booking booking = createBooking(screening, COMPLETED);
        createBookingSeat(booking, seat);

        List<Seat> seats = List.of(seat);
        BookingHoldRequest request = BookingHoldRequest.builder()
                .screeningId(screening.getId())
                .seatIds(seats.stream().map(Seat::getId).collect(Collectors.toList()))
                .build();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 28, 10, 0);

        //when, then
        assertThatThrownBy(() -> bookingService.holdBooking(user.getId(), request, bookingTime))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("불가능한 예매입니다.");
    }

    @DisplayName("예매를 사전등록할 때, 조회된 예매가 취소된 예매일 경우 정상 등록된다.")
    @Test
    void holdBookingWithCanceled() {
        //given
        User user = createUser();
        LocalDateTime startDate = LocalDateTime.of(2024, 11, 29, 10, 0);
        Screening screening = createScreening(startDate);
        Seat seat = createSeat("A", "1");
        Booking booking = createBooking(screening, CANCELED);
        createBookingSeat(booking, seat);

        List<Seat> seats = List.of(seat);
        BookingHoldRequest request = BookingHoldRequest.builder()
                .screeningId(screening.getId())
                .seatIds(seats.stream().map(Seat::getId).collect(Collectors.toList()))
                .build();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 28, 10, 0);

        //when
        Long bookingId = bookingService.holdBooking(user.getId(), request, bookingTime);

        //then
        Optional<Booking> findOptionalBooking = bookingRepository.findByIdWithBookingSeats(bookingId);
        assertThat(findOptionalBooking).isPresent();
        Booking findBooking = findOptionalBooking.get();
        assertThat(findBooking)
                .extracting("user", "screening", "bookingStatus")
                .containsExactly(user, screening, PENDING);
        assertThat(findBooking.getBookingSeats()).hasSize(1)
                .extracting("seat")
                .containsExactlyInAnyOrder(seat);
    }

    @DisplayName("결제 내역을 생성하고, 결제 사후 검증 후 예매를 확정한다.")
    @Test
    void confirmBookingAndPaymentHistory() {
        //given
        User user = createUser();
        Booking booking = createBooking(user, PENDING);

        ConfirmBookingRequest request = ConfirmBookingRequest.builder()
                .bookingId(booking.getId())
                .impId("impId")
                .amount(10000L)
                .bookingNumber("bookingNumber")
                .payTime(1732820400000L)
                .payMethod("card")
                .currency(Currency.KRW)
                .build();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 28, 10, 0);

        doReturn(true)
                .when(paymentHistoryService)
                .validatePaymentAmount(anyString(), anyLong());

        //when
        bookingService.confirmBookingAndPaymentHistory(user.getId(), request, bookingTime);

        //then
        assertThat(booking.getPaymentHistory().getId()).isNotNull();
        assertThat(booking.getPaymentHistory())
                .extracting("user", "payTime", "payStatus")
                .containsExactly(user, LocalDateTime.of(2024, 11, 29, 4, 0), PayStatus.COMPLETED);
        assertThat(booking)
                .extracting("bookingNumber", "bookingStatus")
                .containsExactly("bookingNumber", CONFIRMED);
    }

    @DisplayName("결제 내역을 생성하고, 결제 사후 검증 후 예매를 확정할 때, 결제 사후 검증이 실패한 경우 예매와 결제를 취소한다.")
    @Test
    void confirmBookingAndPaymentHistoryWithFailPayment() {
        //given
        User user = createUser();
        Booking booking = createBooking(user, PENDING);

        ConfirmBookingRequest request = ConfirmBookingRequest.builder()
                .bookingId(booking.getId())
                .impId("impId")
                .amount(10000L)
                .bookingNumber("bookingNumber")
                .payTime(1732820400000L)
                .payMethod("card")
                .currency(Currency.KRW)
                .build();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 28, 10, 0);

        doReturn(false)
                .when(paymentHistoryService)
                .validatePaymentAmount(anyString(), anyLong());
        doReturn(true)
                .when(paymentHistoryService)
                .cancelIamportPayment(anyString(), anyString());

        //when, then
        assertThatThrownBy(() -> bookingService.confirmBookingAndPaymentHistory(user.getId(), request, bookingTime))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("비정상적인 접근입니다. 결제 사후 검증에 실패했습니다.");

        assertThat(booking.getPaymentHistory().getPayStatus()).isEqualTo(PayStatus.FAILED);
        assertThat(booking.getBookingStatus()).isEqualTo(CANCELED);
    }

    @DisplayName("결제 내역을 생성하고, 결제 사후 검증 후 예매를 확정할 때, 유효하지 않은 예매일 경우 예외가 발생한다.")
    @Test
    void confirmBookingAndPaymentHistoryWithNoBooking() {
        //given
        User user = createUser();
        ConfirmBookingRequest request = ConfirmBookingRequest.builder()
                .bookingId(1L)
                .impId("impId")
                .amount(10000L)
                .bookingNumber("bookingNumber")
                .payTime(1732820400000L)
                .payMethod("card")
                .currency(Currency.KRW)
                .build();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 28, 10, 0);

        //when, then
        assertThatThrownBy(() -> bookingService.confirmBookingAndPaymentHistory(user.getId(), request, bookingTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 예매입니다. 예매 정보를 다시 확인해 주세요.");
    }

    @DisplayName("결제 내역을 생성하고, 결제 사후 검증 후 예매를 확정할 때, 판매 대기 중인 예매가 아닐 경우 예외가 발생한다.")
    @Test
    void confirmBookingAndPaymentHistoryWithNoPendingBooking() {
        //given
        User user = createUser();
        Booking booking = createBooking(user, CONFIRMED);
        ConfirmBookingRequest request = ConfirmBookingRequest.builder()
                .bookingId(booking.getId())
                .impId("impId")
                .amount(10000L)
                .bookingNumber("bookingNumber")
                .payTime(1732820400000L)
                .payMethod("card")
                .currency(Currency.KRW)
                .build();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 28, 10, 0);

        //when, then
        assertThatThrownBy(() -> bookingService.confirmBookingAndPaymentHistory(user.getId(), request, bookingTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 예매입니다. 예매 정보를 다시 확인해 주세요.");
    }

    @DisplayName("결제 내역을 생성하고, 결제 사후 검증 후 예매를 확정할 때, 예매의 소유자가 다를 경우 예외가 발생한다.")
    @Test
    void confirmBookingAndPaymentHistoryWithDifferentUser() {
        //given
        User user1 = createUser();
        User user2 = createUser();
        Booking booking = createBooking(user1, CONFIRMED);
        ConfirmBookingRequest request = ConfirmBookingRequest.builder()
                .bookingId(booking.getId())
                .impId("impId")
                .amount(10000L)
                .bookingNumber("bookingNumber")
                .payTime(1732820400000L)
                .payMethod("card")
                .currency(Currency.KRW)
                .build();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 28, 10, 0);

        //when, then
        assertThatThrownBy(() -> bookingService.confirmBookingAndPaymentHistory(user2.getId(), request, bookingTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 예매입니다. 예매 정보를 다시 확인해 주세요.");
    }

    @DisplayName("예매정보를 조회한다.")
    @Test
    void getBooking() {
        //given
        LocalDateTime startDateTime = LocalDateTime.of(2024, 11, 1, 15, 0);
        Booking booking = createBooking(createScreening(startDateTime), LocalDateTime.of(2024, 11, 1, 10, 0));
        booking.addBookingSeat(createBookingSeat("A", "1"));

        //when
        BookingResponse bookingResponse = bookingService.getBooking(booking.getId());

        //then
        assertThat(bookingResponse)
                .extracting("startDate", "startTime", "bookingTime")
                .containsExactly("2024.11.01 (금)", "15:00", "2024.11.01 (금) 10:00");
        assertThat(bookingResponse.getSeats()).hasSize(1)
                .extracting("section", "seatRow")
                .containsExactly(
                        tuple("A", "1")
                );
    }

    @DisplayName("예매정보를 조회할 때, 유효하지 않은 예매일 경우 예외가 발생한다. ")
    @Test
    void getBookingWithNoBookingId() {
        //when, then
        assertThatThrownBy(() -> bookingService.getBooking(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 예매입니다. 예매 정보를 다시 확인해 주세요.");
    }

    @DisplayName("회원의 전체 예매 내역을 조회한다.")
    @Test
    void getBookingHistory() {
        //given
        User user = createUser();
        Screening screening = createScreening(LocalDateTime.of(2024, 11, 2, 15, 0));
        LocalDateTime bookingTime = LocalDateTime.of(2024, 11, 1, 0, 0);

        Booking booking1 = createBooking(user, screening, CONFIRMED, bookingTime);
        booking1.addBookingSeat(createBookingSeat("A", "1"));

        Booking booking2 = createBooking(user, screening, CONFIRMED, bookingTime);
        booking2.addBookingSeat(createBookingSeat("A", "1"));

        Booking booking3 = createBooking(user, screening, CANCELED, bookingTime);
        booking3.addBookingSeat(createBookingSeat("A", "1"));
        booking3.addBookingSeat(createBookingSeat("A", "2"));

        //when
        Map<BookingStatus, List<BookingWithDateResponse>> bookingStatusMap = bookingService.getBookingHistory(user.getId());

        //then
        assertThat(bookingStatusMap).hasSize(2);
        assertThat(bookingStatusMap.get(CONFIRMED)).hasSize(2);

        assertThat(bookingStatusMap.get(CANCELED)).hasSize(1);
        assertThat(bookingStatusMap.get(CANCELED).getFirst())
                .extracting("startDate", "startTime", "bookingTime")
                .containsExactly("2024.11.02 (토)", "15:00", "2024.11.01 (금) 00:00");
        assertThat(bookingStatusMap.get(CANCELED).getFirst().getSeats()).hasSize(2)
                .extracting("section", "seatRow")
                .containsExactlyInAnyOrder(
                        tuple("A", "1"),
                        tuple("A", "2")
                );
    }

    @DisplayName("예매와 결제 취소 후 예매 내역을 조회한다.")
    @Test
    void cancelBookingAndPaymentGetBookingHistory() {
        //given
        User user = createUser();
        Booking booking = createBooking(user, CONFIRMED);
        booking.addBookingSeat(createBookingSeat("A", "1"));
        doReturn(true)
                .when(paymentHistoryService)
                .cancelIamportPayment(anyString(), anyString());

        //when
        Map<BookingStatus, List<BookingWithDateResponse>> bookingStatusMap = bookingService.cancelBookingAndPaymentGetBookingHistory(user.getId(), booking.getId());

        //then
        assertThat(booking.getPaymentHistory().getPayStatus()).isEqualTo(PayStatus.CANCELED);
        assertThat(booking.getBookingStatus()).isEqualTo(CANCELED);
        assertThat(bookingStatusMap).hasSize(1);
    }

    @DisplayName("예매와 결제 취소 후 예매 내역을 조회할 때, 유효하지 않은 예매일 경우 예외가 발생한다.")
    @Test
    void cancelBookingAndPaymentGetBookingHistoryWithNoBooking() {
        //given
        User user = createUser();

        //when, then
        assertThatThrownBy(() -> bookingService.cancelBookingAndPaymentGetBookingHistory(user.getId(), 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 예매입니다. 예매 정보를 다시 확인해 주세요.");
    }

    @DisplayName("예매와 결제 취소 후 예매 내역을 조회할 때, 이미 취소된 예매일 경우 예외가 발생한다.")
    @Test
    void cancelBookingAndPaymentGetBookingHistoryWithCanceledBooking() {
        //given
        User user = createUser();
        Booking booking = createBooking(user, CANCELED);

        //when, then
        assertThatThrownBy(() -> bookingService.cancelBookingAndPaymentGetBookingHistory(user.getId(), booking.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("취소된 예매입니다.");
    }

    @DisplayName("예매와 결제 취소 후 예매 내역을 조회할 때, 사용자가 예매 취소 권한이 없는 경우 예외가 발생한다.")
    @Test
    void cancelBookingAndPaymentGetBookingHistoryWithDifferentUser() {
        //given
        User user1 = createUser();
        User user2 = createUser();
        Booking booking = createBooking(user1, CONFIRMED);

        //when, then
        assertThatThrownBy(() -> bookingService.cancelBookingAndPaymentGetBookingHistory(user2.getId(), booking.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예매 취소 권한이 없습니다.");
    }

    private User createUser() {
        User user = User.builder()
                .build();
        return userRepository.save(user);
    }
    private Movie createMovie() {
        Movie movie = Movie.builder()
                .build();
        return movieRepository.save(movie);
    }
    private Theater createTheater() {
        Theater theater = Theater.builder()
                .build();
        return theaterRepository.save(theater);
    }
    private Hall createHall() {
        Hall hall = Hall.builder()
                .screeningType(ScreeningType.TWO_D)
                .theater(createTheater())
                .build();
        return hallRepository.save(hall);
    }
    private PaymentHistory createPaymentHistory() {
        PaymentHistory paymentHistory = PaymentHistory.builder()
                .impId("impId")
                .payStatus(PayStatus.COMPLETED)
                .build();
        return paymentHistoryRepository.save(paymentHistory);
    }
    private Seat createSeat(String section, String seatRow) {
        SeatLocation seatLocation = new SeatLocation(section, seatRow);
        Seat seat = Seat.builder()
                .seatLocation(seatLocation)
                .build();
        return seatRepository.save(seat);
    }
    private Screening createScreening(LocalDateTime startDateTime) {
        Screening screening = Screening.builder()
                .movie(createMovie())
                .hall(createHall())
                .startTime(startDateTime)
                .build();
        return screeningRepository.save(screening);
    }
    private Booking createBooking(Screening screening, BookingStatus bookingStatus) {
        Booking booking = Booking.builder()
                .screening(screening)
                .bookingStatus(bookingStatus)
                .build();
        return bookingRepository.save(booking);
    }
    private Booking createBooking(User user, BookingStatus bookingStatus) {
        Booking booking = Booking.builder()
                .user(user)
                .screening(createScreening(LocalDateTime.of(2024, 11, 29, 10, 0)))
                .paymentHistory(createPaymentHistory())
                .bookingStatus(bookingStatus)
                .bookingTime(LocalDateTime.of(2024, 11, 28, 10, 0))
                .build();
        return bookingRepository.save(booking);
    }
    private Booking createBooking(Screening screening, LocalDateTime bookingTime) {
        Booking booking = Booking.builder()
                .user(createUser())
                .screening(screening)
                .paymentHistory(createPaymentHistory())
                .bookingTime(bookingTime)
                .build();
        return bookingRepository.save(booking);
    }
    private Booking createBooking(User user, Screening screening, BookingStatus bookingStatus, LocalDateTime bookingTime) {
        Booking booking = Booking.builder()
                .user(user)
                .screening(screening)
                .paymentHistory(createPaymentHistory())
                .bookingStatus(bookingStatus)
                .bookingTime(bookingTime)
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
    private BookingSeat createBookingSeat(String section, String seatRow) {
        BookingSeat bookingSeat = BookingSeat.builder()
                .seat(createSeat(section, seatRow))
                .build();
        return bookingSeatRepository.save(bookingSeat);
    }

}