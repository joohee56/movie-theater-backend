package mt.movie_theater.api.hall.response;

import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.api.theater.response.TheaterResponse;
import mt.movie_theater.domain.hall.Hall;

@Getter
public class HallResponse {
    private Long id;
    private TheaterResponse theater;
    private String name;
    private Integer totalSeats;
    private String screeningType;
    private int hallTypeModifier;

    @Builder
    public HallResponse(Long id, TheaterResponse theater, String name, Integer totalSeats, String screeningType, int hallTypeModifier) {
        this.id = id;
        this.theater = theater;
        this.name = name;
        this.totalSeats = totalSeats;
        this.screeningType = screeningType;
        this.hallTypeModifier = hallTypeModifier;
    }

    public static HallResponse create(Hall hall) {
        return HallResponse.builder()
                .id(hall.getId())
                .theater(TheaterResponse.create(hall.getTheater()))
                .name(hall.getName())
                .totalSeats(hall.getSeats().size())
                .screeningType(hall.getScreeningType().getText())
                .hallTypeModifier(hall.getHallTypeModifier())
                .build();
    }
}
