package mt.movie_theater.api.hall.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.api.seat.response.SeatResponse;
import mt.movie_theater.domain.seat.SeatLocation;

@Getter
public class HallSeatsResponse {
    private Long hallId;
    private List<SeatLocation> seats;

    @Builder
    public HallSeatsResponse(Long hallId, List<SeatLocation> seats) {
        this.hallId = hallId;
        this.seats = seats;
    }

    public static HallSeatsResponse create(Long hallId, List<SeatResponse> seats) {
        return HallSeatsResponse.builder()
                .seats(seats.stream().map(seatResponse -> new SeatLocation(seatResponse.getSection(), seatResponse.getSeatRow())).collect(Collectors.toList()))
                .hallId(hallId).build();
    }

}
