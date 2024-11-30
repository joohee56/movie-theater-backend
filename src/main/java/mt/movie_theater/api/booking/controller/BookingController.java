package mt.movie_theater.api.booking.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mt.movie_theater.api.apiResponse.ApiResponse;
import mt.movie_theater.api.booking.request.BookingHoldRequest;
import mt.movie_theater.api.booking.response.BookingResponse;
import mt.movie_theater.api.booking.response.BookingWithDateResponse;
import mt.movie_theater.api.booking.service.BookingService;
import mt.movie_theater.api.payment.request.ConfirmBookingRequest;
import mt.movie_theater.api.user.annotation.Login;
import mt.movie_theater.api.user.annotation.LoginCheck;
import mt.movie_theater.domain.booking.BookingStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    /**
     * 임시 예매
     * 좌석 예매 가능 여부 조회
     * 예매 테이블에 PENDING 상태로 데이터 생성
     * TODO: 사용자가 결제 진행 도중 브라우저를 닫으면 CANCELED 로 변경
     */
    @LoginCheck
    @PostMapping("/booking/hold")
    public ApiResponse<Long> holdBooking(@Login Long userId, @Valid @RequestBody BookingHoldRequest request) {
        LocalDateTime bookingTime = LocalDateTime.now();
        return ApiResponse.ok(bookingService.holdBooking(userId, request, bookingTime));
    }

    /**
     * 예매 사후 검증 후 예매 확정
     * 결제 내역 테이블 데이터 생성
     */
    @LoginCheck
    @PostMapping("/booking/confirm")
    public ApiResponse<BookingResponse> confirmBookingAndPaymentHistory(@Login Long userId, @Valid @RequestBody ConfirmBookingRequest request) {
        LocalDateTime bookingTime = LocalDateTime.now();
        return ApiResponse.ok(bookingService.confirmBookingAndPaymentHistory(userId, request, bookingTime));
    }

    @LoginCheck
    @GetMapping("/booking/{bookingId}")
    public ApiResponse<BookingResponse> getBooking(@PathVariable("bookingId") Long bookingId) {
        return ApiResponse.ok(bookingService.getBooking(bookingId));
    }

    @LoginCheck
    @GetMapping("/user")
    public ApiResponse<Map<BookingStatus, List<BookingWithDateResponse>>> getBookingHistory(@Login Long userId) {
        return ApiResponse.ok(bookingService.getBookingHistory(userId));
    }

    @LoginCheck
    @GetMapping("/cancel/{bookingId}")
    public ApiResponse<Map<BookingStatus, List<BookingWithDateResponse>>> cancelBookingAndGetBookingHistory(@Login Long userId, @PathVariable("bookingId") Long bookingId) {
        return ApiResponse.ok(bookingService.cancelBookingAndPaymentGetBookingHistory(userId, bookingId));
    }
}
