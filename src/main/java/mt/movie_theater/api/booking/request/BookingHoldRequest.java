package mt.movie_theater.api.booking.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookingHoldRequest {

    @NotNull(message = "상영시간 ID는 필수 입력값입니다.")
    private Long screeningId;

    @NotEmpty(message = "좌석 리스트는 필수 입력값입니다.")
    private List<Long> seatIds;

    @Builder
    public BookingHoldRequest(Long screeningId, List<Long> seatIds) {
        this.screeningId = screeningId;
        this.seatIds = seatIds;
    }
}
