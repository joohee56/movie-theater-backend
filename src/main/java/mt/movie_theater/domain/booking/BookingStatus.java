package mt.movie_theater.domain.booking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookingStatus {
    PENDING("결제 대기"),
    CONFIRMED("확정"),
    CANCELED("취소"),
    EXPIRED("기간 만료"),
    COMPLETED("관람 완료");
    private final String text;
}
