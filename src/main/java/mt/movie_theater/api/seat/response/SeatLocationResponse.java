package mt.movie_theater.api.seat.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mt.movie_theater.domain.seat.Seat;

@Getter
@NoArgsConstructor
public class SeatLocationResponse {
    private String section;
    private String seatRow;

    @Builder
    private SeatLocationResponse(String section, String seatRow) {
        this.section = section;
        this.seatRow = seatRow;
    }

    public static SeatLocationResponse create(Seat seat) {
        return SeatLocationResponse.builder()
                .section(seat.getSeatLocation().getSection())
                .seatRow(seat.getSeatLocation().getSeatRow())
                .build();
    }
}
