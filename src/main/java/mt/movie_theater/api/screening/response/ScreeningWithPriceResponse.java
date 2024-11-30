package mt.movie_theater.api.screening.response;

import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.domain.screening.Screening;
import mt.movie_theater.util.DateUtil;

@Getter
public class ScreeningWithPriceResponse {
    private String movieTitle;
    private String ageRatingDisplay;
    private String screeningTypeDisplay;
    private String posterUrl;
    private String theaterName;
    private String hallName;
    private String startDate;
    private String startTime;
    private String endTime;
    private int totalPrice;

    @Builder
    private ScreeningWithPriceResponse(String movieTitle, String ageRatingDisplay, String screeningTypeDisplay,
                                      String posterUrl, String theaterName, String hallName, String startDate,
                                      String startTime, String endTime, int totalPrice) {
        this.movieTitle = movieTitle;
        this.ageRatingDisplay = ageRatingDisplay;
        this.screeningTypeDisplay = screeningTypeDisplay;
        this.posterUrl = posterUrl;
        this.theaterName = theaterName;
        this.hallName = hallName;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
    }

    public static ScreeningWithPriceResponse create(Screening screening) {
        return ScreeningWithPriceResponse.builder()
                .movieTitle(screening.getMovie().getTitle())
                .ageRatingDisplay(screening.getMovie().getAgeRating().getDisplay())
                .screeningTypeDisplay(screening.getMovie().getScreeningType().getText())
                .posterUrl(screening.getMovie().getPosterUrl())
                .theaterName(screening.getHall().getTheater().getName())
                .hallName(screening.getHall().getName())
                .startDate(DateUtil.formatToStartDate(screening.getStartTime()))
                .startTime(DateUtil.formatToHourAndMinute(screening.getStartTime()))
                .endTime(DateUtil.formatToHourAndMinute(screening.getEndTime()))
                .totalPrice(screening.getTotalPrice())
                .build();
    }

}
