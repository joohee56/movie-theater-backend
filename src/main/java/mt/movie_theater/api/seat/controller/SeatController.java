package mt.movie_theater.api.seat.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mt.movie_theater.api.apiResponse.ApiResponse;
import mt.movie_theater.api.seat.request.SeatListCreateRequest;
import mt.movie_theater.api.seat.response.SeatResponse;
import mt.movie_theater.api.seat.response.SeatSummaryResponse;
import mt.movie_theater.api.seat.service.SeatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seats")
public class SeatController {
    private final SeatService seatService;

    @PostMapping("/new")
    public ApiResponse<List<SeatResponse>> createSeatList(@Valid @RequestBody SeatListCreateRequest request) {
        List<SeatResponse> seats = seatService.createSeatList(request.getHallId(), request.getRows(), request.getColumns());
        return ApiResponse.ok(seats);
    }

    @GetMapping("")
    public ApiResponse<Map<String, List<SeatSummaryResponse>>> getSeatList(@RequestParam(name = "screeningId") Long screeningId, @RequestParam(name = "hallId") Long hallId) {
        return ApiResponse.ok(seatService.getSeatList(screeningId, hallId));
    }
}
