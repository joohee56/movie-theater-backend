package mt.movie_theater.domain.seat;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class SeatLocation {
    @Column(length = 1)
    private String section;
    @Column(length = 50)
    private String seatRow;

    public SeatLocation(String section, String seatRow) {
        this.section = section;
        this.seatRow = seatRow;
    }
}
