package mt.movie_theater.api.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mt.movie_theater.api.booking.request.BookingHoldRequest;
import mt.movie_theater.api.booking.response.BookingResponse;
import mt.movie_theater.api.booking.response.BookingWithDateResponse;
import mt.movie_theater.api.payment.request.ConfirmBookingRequest;
import mt.movie_theater.api.payment.service.PaymentHistoryService;
import mt.movie_theater.api.seat.service.SeatService;
import mt.movie_theater.domain.booking.Booking;
import mt.movie_theater.domain.booking.BookingRepository;
import mt.movie_theater.domain.booking.BookingStatus;
import mt.movie_theater.domain.payment.PaymentHistory;
import mt.movie_theater.domain.payment.PaymentHistoryRepository;
import mt.movie_theater.domain.screening.Screening;
import mt.movie_theater.domain.screening.ScreeningRepository;
import mt.movie_theater.domain.seat.Seat;
import mt.movie_theater.domain.user.User;
import mt.movie_theater.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ScreeningRepository screeningRepository;
    private final UserRepository userRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentHistoryService paymentHistoryService;
    private final SeatService seatService;
    private final Lock fairLock = new ReentrantLock(true);

    @Transactional
    public Long holdBooking(Long userId, BookingHoldRequest request, LocalDateTime bookingTime) {
        try {
            if (!fairLock.tryLock(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("접속이 지연되고 있습니다. 나중에 다시 시도해 주세요.");
            }
            User user = validateUser(userId);
            Screening screening = validateScreening(request.getScreeningId(), bookingTime);
            List<Seat> seats = seatService.validateSeats(request.getSeatIds(), screening);
            Booking booking = Booking.hold(user, screening, seats);
            return bookingRepository.save(booking).getId();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            fairLock.unlock();
        }
    }

    /**
     * 결제내역 생성, 결제 사후 검증 후 예매 확정
     */
    @Transactional
    public BookingResponse confirmBookingAndPaymentHistory(Long userId, ConfirmBookingRequest request, LocalDateTime bookingTime) {
        User user = validateUser(userId);
        Booking booking = validateBookingForConfirm(user.getId(), request.getBookingId());
        PaymentHistory paymentHistory = paymentHistoryRepository.save(PaymentHistory.create(request, user));
        booking.initPaymentHistory(paymentHistory);

        if (!paymentHistoryService.validatePaymentAmount(request.getImpId(), request.getAmount())) {
            paymentHistoryService.failPayment(request.getImpId(), paymentHistory, "비정상적인 접근입니다. 결제 요청이 유효하지 않습니다.");
            booking.cancel();
            throw new IllegalStateException("비정상적인 접근입니다. 결제 사후 검증에 실패했습니다.");
        }

        booking.confirm(request.getBookingNumber(), bookingTime);
        return BookingResponse.create(booking);
    }

    public BookingResponse getBooking(Long bookingId) {
        try {
            fairLock.lock();
            Optional<Booking> booking = bookingRepository.findByIdWithBookingSeats(bookingId);
            if (booking.isEmpty()) {
                throw new IllegalArgumentException("유효하지 않은 예매입니다. 예매 정보를 다시 확인해 주세요.");
            }
            return BookingResponse.create(booking.get());
        } finally {
            fairLock.unlock();
        }
    }

    /**
     * 회원의 전체 예매 내역 조회 (confirmed, canceled 기준)
     */
    public Map<BookingStatus, List<BookingWithDateResponse>> getBookingHistory(Long userId) {
        List<Booking> bookings = bookingRepository.findAllWithBookingSeatsByUserId(userId);
        return bookings.stream()
                .collect(Collectors.groupingBy(
                        Booking::getBookingStatus,
                        Collectors.mapping(BookingWithDateResponse::create, Collectors.toList())));
    }

    /**
     * 예매와 결제 취소 후 예매 내역 조회
     */
    @Transactional
    public Map<BookingStatus, List<BookingWithDateResponse>> cancelBookingAndPaymentGetBookingHistory(Long userId, Long bookingId) {
        User user = validateUser(userId);
        Booking booking = validateBookingForCancel(bookingId, user.getId());
        paymentHistoryService.cancelPayment(booking.getPaymentHistory().getImpId(), booking.getPaymentHistory(), "예매를 취소합니다.");
        booking.cancel();
        return getBookingHistory(userId);
    }

    private User validateUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 사용자입니다. 사용자 정보를 다시 확인해 주세요.");
        }
        return user.get();
    }

    private Screening validateScreening(Long screeningId, LocalDateTime bookingTime) {
        Optional<Screening> screening = screeningRepository.findById(screeningId);
        if (screening.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 상영시간입니다. 상영시간 정보를 다시 확인해 주세요.");
        }
        if (screening.get().getStartTime().isBefore(bookingTime)) {
            throw new IllegalArgumentException("상영 시작 시간이 지났습니다. 다른 상영 시간을 선택해 주세요.");
        }
        return screening.get();
    }

    private Booking validateBookingForConfirm(Long userId, Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty() || !optionalBooking.get().getBookingStatus().equals(BookingStatus.PENDING) || !optionalBooking.get().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("유효하지 않은 예매입니다. 예매 정보를 다시 확인해 주세요.");
        }
        return optionalBooking.get();
    }

    private Booking validateBookingForCancel(Long bookingId, Long userId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 예매입니다. 예매 정보를 다시 확인해 주세요.");
        }
        if (optionalBooking.get().getBookingStatus().equals(BookingStatus.CANCELED)) {
            throw new IllegalArgumentException("취소된 예매입니다.");
        }
        if (!optionalBooking.get().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("예매 취소 권한이 없습니다.");
        }
        return optionalBooking.get();
    }

}
