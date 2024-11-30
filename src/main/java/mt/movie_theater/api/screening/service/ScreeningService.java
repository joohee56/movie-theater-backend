package mt.movie_theater.api.screening.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mt.movie_theater.api.screening.request.ScreeningCreateRequest;
import mt.movie_theater.api.screening.response.FullScreeningResponse;
import mt.movie_theater.api.screening.response.ScreeningResponse;
import mt.movie_theater.api.screening.response.ScreeningWithPriceResponse;
import mt.movie_theater.api.screening.response.TheaterScreeningCountResponse;
import mt.movie_theater.api.theater.response.RegionScreeningCountResponse;
import mt.movie_theater.domain.hall.Hall;
import mt.movie_theater.domain.hall.HallRepository;
import mt.movie_theater.domain.movie.Movie;
import mt.movie_theater.domain.movie.MovieRepository;
import mt.movie_theater.domain.screening.Screening;
import mt.movie_theater.domain.screening.ScreeningRepository;
import mt.movie_theater.domain.screening.dto.RegionScreeningCountDto;
import mt.movie_theater.domain.screening.dto.TheaterScreeningCountDto;
import mt.movie_theater.domain.theater.Region;
import mt.movie_theater.domain.theater.Theater;
import mt.movie_theater.domain.theater.TheaterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScreeningService {
    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;
    private final TheaterRepository theaterRepository;

    @Transactional
    public ScreeningResponse createScreening(ScreeningCreateRequest request) {
        Movie movie = validateMovie(request.getMovieId());
        Hall hall = validateHall(request.getHallId());
        LocalDateTime endTime = calculateEndTime(request.getStartTime(), movie.getDurationMinutes());
        int ticketTotalPrice = TicketPriceCalculator.calculateFinalPrice(movie.getStandardPrice(), hall.getHallTypeModifier(), request.getStartTime());
        Screening screening = Screening.builder()
                                .movie(movie)
                                .hall(hall)
                                .startTime(request.getStartTime())
                                .endTime(endTime)
                                .totalPrice(ticketTotalPrice)
                                .build();
        return ScreeningResponse.create(screeningRepository.save(screening));
    }

    private Movie validateMovie(Long movieId) {
        Optional<Movie> movie = movieRepository.findById(movieId);
        if (movie.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 영화입니다. 영화 정보를 다시 확인해 주세요.");
        }
        return movie.get();
    }

    private Hall validateHall(Long hallId) {
        Optional<Hall> hall = hallRepository.findById(hallId);
        if (hall.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 상영관입니다. 상영관 정보를 다시 확인해 주세요.");
        }
        return hall.get();
    }

    private LocalDateTime calculateEndTime(LocalDateTime startTime, Duration duration) {
        return startTime.plus(duration);
    }

    /**
     * 전체 지역 리스트 조회, 각 지역별 상영시간 갯수
     */
    public List<RegionScreeningCountResponse> getRegionsWithScreeningCount(LocalDate date, Long movieId) {
        LocalDateTime startDateTime = getStartDateTime(date);
        LocalDateTime endDateTime = getEndDateTime(date);

        List<RegionScreeningCountDto> regionCountDtos = screeningRepository.countScreeningByRegion(startDateTime, endDateTime, movieId);
        Map<Region, Long> regionCountMap = regionCountDtos.stream()
                .collect(Collectors.toMap(RegionScreeningCountDto::getRegion, RegionScreeningCountDto::getCount));

        return Arrays.stream(Region.values())
                .map(region -> RegionScreeningCountResponse.create(region, regionCountMap.getOrDefault(region, 0L)))
                .collect(Collectors.toList());
    }

    /**
     * 지역별 영화관 리스트 조회, 각 영화관별 상영시간 갯수
     */
    public List<TheaterScreeningCountResponse> getTheatersWithScreeningCount(LocalDate date, Region region, Long movieId) {
        LocalDateTime startDateTime = getStartDateTime(date);
        LocalDateTime endDateTime = getEndDateTime(date);
        Map<Theater, Long> screeningCountMap = createScreeningCountMap(startDateTime, endDateTime, region, movieId);

        List<Theater> theaters = theaterRepository.findALlByRegion(region);
        return theaters.stream()
                .map(theater -> TheaterScreeningCountResponse.create(theater, screeningCountMap.getOrDefault(theater,
                        0L)))
                .collect(Collectors.toList());
    }

    private Map<Theater, Long> createScreeningCountMap(LocalDateTime startDateTime, LocalDateTime endDateTime, Region region, Long movieId) {
        List<TheaterScreeningCountDto> theaterScreeningCounts = screeningRepository.findTheaterScreeningCounts(startDateTime, endDateTime, region, movieId);
        return theaterScreeningCounts.stream()
                .collect(Collectors.toMap(TheaterScreeningCountDto::getTheater, TheaterScreeningCountDto::getCount));
    }

    /**
     * 상영시간 리스트 조회
     */
    public List<FullScreeningResponse> getScreenings(LocalDate date, Long movieId, Long theaterId) {
        LocalDateTime startDateTime = getStartDateTime(date);
        LocalDateTime endDateTime = getEndDateTime(date);
        List<Screening> screenings = screeningRepository.findAllByDateAndTheaterIdAndOptionalMovieId(startDateTime, endDateTime, movieId, theaterId);
        return screenings.stream()
                .map(FullScreeningResponse::create)
                .collect(Collectors.toList());
    }

    /**
     * 상영시간과 최종 결제 금액 조회
     */
    public ScreeningWithPriceResponse getScreeningWithTotalPrice(Long screeningId) {
        Screening screening = validateScreening(screeningId);
        return ScreeningWithPriceResponse.create(screening);
    }

    private Screening validateScreening(Long screeningId) {
        Optional<Screening> optionalScreening = screeningRepository.findById(screeningId);
        if (optionalScreening.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 상영시간입니다. 상영시간 정보를 다시 확인해 주세요.");
        }
        return optionalScreening.get();
    }

    private LocalDateTime getStartDateTime(LocalDate date) {
        return date.atStartOfDay(); // 00:00:00
    }

    private LocalDateTime getEndDateTime(LocalDate date) {
        return date.atTime(LocalTime.MAX); // 23:59:59
    }

}
