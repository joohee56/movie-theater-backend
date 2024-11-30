package mt.movie_theater.api.seat.response;

import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.domain.seat.Seat;

@Getter
public class SeatResponse {
    private Long id;
    private Long hallId;
    private String section;
    private String seatRow;

    @Builder
    public SeatResponse(Long id, Long hallId, String section, String seatRow) {
        this.id = id;
        this.hallId = hallId;
        this.section = section;
        this.seatRow = seatRow;
    }

    public static SeatResponse create(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .hallId(seat.getHall().getId())
                .section(seat.getSeatLocation().getSection())
                .seatRow(seat.getSeatLocation().getSeatRow())
                .build();
    }
}
