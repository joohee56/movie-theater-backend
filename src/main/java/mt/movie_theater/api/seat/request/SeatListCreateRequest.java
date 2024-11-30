package mt.movie_theater.api.seat.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SeatListCreateRequest {

    @NotNull(message = "상영관 ID는 필수 입력값입니다.")
    private Long hallId;

    @PositiveOrZero(message = "좌석의 행 갯수는 0 또는 양수여야 합니다.")
    private int rows;

    @PositiveOrZero(message = "좌석의 열 갯수는 0 또는 양수여야 합니다.")
    private int columns;

    @Builder
    public SeatListCreateRequest(Long hallId, int rows, int columns) {
        this.hallId = hallId;
        this.rows = rows;
        this.columns = columns;
    }
}
