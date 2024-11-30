package mt.movie_theater.api.screening.response;

import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.domain.theater.Theater;

@Getter
public class TheaterScreeningCountResponse {
    private Long theaterId;
    private String theaterName;
    private Long screeningCount;

    @Builder
    public TheaterScreeningCountResponse(Long theaterId, String theaterName, Long screeningCount) {
        this.theaterId = theaterId;
        this.theaterName = theaterName;
        this.screeningCount = screeningCount;
    }

    public static TheaterScreeningCountResponse create(Theater theater,Long count) {
        return TheaterScreeningCountResponse.builder()
                .theaterId(theater.getId())
                .theaterName(theater.getName())
                .screeningCount(count)
                .build();
    }
}
