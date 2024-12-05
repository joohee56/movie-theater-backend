package mt.movie_theater.api.seat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.domain.seat.Seat;

@Getter
public class SeatSummaryResponse {
    private Long seatId;
    private String section;
    private String seatRow;
    @JsonProperty("isBooked")
    private boolean isBooked;

    @Builder
    private SeatSummaryResponse(Long seatId, String section, String seatRow, boolean isBooked) {
        this.seatId = seatId;
        this.section = section;
        this.seatRow = seatRow;
        this.isBooked = isBooked;
    }

    public static SeatSummaryResponse create(Seat seat, boolean isBooked) {
        return SeatSummaryResponse.builder()
                .seatId(seat.getId())
                .section(seat.getSeatLocation().getSection())
                .seatRow(seat.getSeatLocation().getSeatRow())
                .isBooked(isBooked)
                .build();
    }
}
