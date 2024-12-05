package mt.movie_theater.api.hall.service;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import mt.movie_theater.api.hall.request.HallCreateRequest;
import mt.movie_theater.api.hall.request.HallSeatsCreateRequest;
import mt.movie_theater.api.hall.response.HallResponse;
import mt.movie_theater.api.hall.response.HallSeatsResponse;
import mt.movie_theater.api.seat.response.SeatResponse;
import mt.movie_theater.api.seat.service.SeatService;
import mt.movie_theater.domain.hall.Hall;
import mt.movie_theater.domain.hall.HallRepository;
import mt.movie_theater.domain.theater.Theater;
import mt.movie_theater.domain.theater.TheaterRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HallService {
    private final HallRepository hallRepository;
    private final TheaterRepository theaterRepository;
    private final SeatService seatService;

    public HallResponse createHall(HallCreateRequest request) {
        Theater theater = validateTheater(request.getTheaterId());
        Hall hall = request.toEntity(theater);
        return HallResponse.create(hallRepository.save(hall));
    }

    public HallSeatsResponse createHallWithSeats(@Valid HallSeatsCreateRequest request) {
        Theater theater = validateTheater(request.getTheaterId());
        Hall hall = hallRepository.save(request.toEntity(theater));
        List<SeatResponse> seats = seatService.createSeatList(hall.getId(), request.getRows(), request.getColumns());
        return HallSeatsResponse.create(hall.getId(), seats);
    }

    public List<HallResponse> getHalls(Long theaterId) {
        List<Hall> halls = hallRepository.findAllByTheaterId(theaterId);
        return halls.stream().map(HallResponse::create).collect(Collectors.toList());
    }

    private Theater validateTheater(Long theaterId) {
        Optional<Theater> theater = theaterRepository.findById(theaterId);
        if(theater.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 영화관입니다. 영화관 정보를 다시 확인해 주세요.");
        }
        return theater.get();
    }
}
