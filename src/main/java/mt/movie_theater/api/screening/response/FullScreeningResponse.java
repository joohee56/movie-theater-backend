package mt.movie_theater.api.screening.response;

import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.domain.screening.Screening;
import mt.movie_theater.util.DateUtil;

@Getter
public class FullScreeningResponse {
    private Long screeningId;
    private String startTime;
    private String endTime;
    private String movieTitle;
    private String screeningTypeDisplay;
    private String theaterName;
    private String hallName;
    private Long hallId;

    @Builder
    private FullScreeningResponse(Long screeningId, String startTime, String endTime, String movieTitle,
                                 String screeningTypeDisplay, String theaterName, String hallName, Long hallId) {
        this.screeningId = screeningId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.movieTitle = movieTitle;
        this.screeningTypeDisplay = screeningTypeDisplay;
        this.theaterName = theaterName;
        this.hallName = hallName;
        this.hallId = hallId;
    }

    public static FullScreeningResponse create(Screening screening) {
        return FullScreeningResponse.builder()
                .screeningId(screening.getId())
                .startTime(DateUtil.formatToHourAndMinute(screening.getStartTime()))
                .endTime(DateUtil.formatToHourAndMinute(screening.getEndTime()))
                .movieTitle(screening.getMovie().getTitle())
                .screeningTypeDisplay(screening.getHall().getScreeningType().getText())
                .theaterName(screening.getHall().getTheater().getName())
                .hallName(screening.getHall().getName())
                .hallId(screening.getHall().getId())
                .build();
    }
}
