package mt.movie_theater.api.booking.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.api.seat.response.SeatLocationResponse;
import mt.movie_theater.domain.booking.Booking;
import mt.movie_theater.util.DateUtil;

@Getter
public class BookingResponse {
    private Long id;
    private String bookingNumber;
    private String posterUrl;
    private String movieTitle;
    private String screeningTypeDisplay;
    private String theaterName;
    private String hallName;
    private String startDate;
    private String startTime;
    private String userEmail;
    private Long totalPrice;
    private String bookingTime;
    private List<SeatLocationResponse> seats;

    @Builder
    public BookingResponse(Long id, String bookingNumber, String posterUrl, String movieTitle,
                           String screeningTypeDisplay,
                           String theaterName, String hallName, String startDate, String startTime, String userEmail,
                           Long totalPrice, String bookingTime, List<SeatLocationResponse> seats) {
        this.id = id;
        this.bookingNumber = bookingNumber;
        this.posterUrl = posterUrl;
        this.movieTitle = movieTitle;
        this.screeningTypeDisplay = screeningTypeDisplay;
        this.theaterName = theaterName;
        this.hallName = hallName;
        this.startDate = startDate;
        this.startTime = startTime;
        this.userEmail = userEmail;
        this.totalPrice = totalPrice;
        this.bookingTime = bookingTime;
        this.seats = seats;
    }

    public static BookingResponse create(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingNumber(booking.getBookingNumber())
                .posterUrl(booking.getScreening().getMovie().getPosterUrl())
                .movieTitle(booking.getScreening().getMovie().getTitle())
                .screeningTypeDisplay(booking.getScreening().getHall().getScreeningType().getText())
                .theaterName(booking.getScreening().getHall().getTheater().getName())
                .hallName(booking.getScreening().getHall().getName())
                .startDate(DateUtil.formatToStartDate(booking.getScreening().getStartTime()))
                .startTime(DateUtil.formatToHourAndMinute(booking.getScreening().getStartTime()))
                .userEmail(booking.getUser().getEmail())
                .totalPrice(booking.getPaymentHistory().getAmount())
                .bookingTime(DateUtil.formatToDateAndHourAndMinute(booking.getBookingTime()))
                .seats(booking.getBookingSeats().stream()
                        .map(BookingSeat -> SeatLocationResponse.create(BookingSeat.getSeat()))
                        .collect(Collectors.toList()))
                .build();
    }
}
