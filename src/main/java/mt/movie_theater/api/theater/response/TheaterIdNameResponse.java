package mt.movie_theater.api.theater.response;

import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.domain.theater.Theater;

@Getter
public class TheaterIdNameResponse {
    private Long theaterId;
    private String theaterName;

    @Builder
    private TheaterIdNameResponse(Long theaterId, String theaterName) {
        this.theaterId = theaterId;
        this.theaterName = theaterName;
    }

    public static TheaterIdNameResponse create(Theater theater) {
        return TheaterIdNameResponse.builder()
                .theaterId(theater.getId())
                .theaterName(theater.getName())
                .build();
    }
}
