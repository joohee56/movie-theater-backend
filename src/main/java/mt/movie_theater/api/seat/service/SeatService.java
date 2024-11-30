package mt.movie_theater.api.seat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mt.movie_theater.api.exception.DuplicateSeatBookingException;
import mt.movie_theater.api.seat.response.SeatResponse;
import mt.movie_theater.api.seat.response.SeatSummaryResponse;
import mt.movie_theater.domain.booking.BookingStatus;
import mt.movie_theater.domain.bookingseat.BookingSeat;
import mt.movie_theater.domain.bookingseat.BookingSeatRepository;
import mt.movie_theater.domain.hall.Hall;
import mt.movie_theater.domain.hall.HallRepository;
import mt.movie_theater.domain.screening.Screening;
import mt.movie_theater.domain.seat.Seat;
import mt.movie_theater.domain.seat.SeatLocation;
import mt.movie_theater.domain.seat.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {
    private final SeatRepository seatRepository;
    private final HallRepository hallRepository;
    private final BookingSeatRepository bookingSeatRepository;

    @Transactional
    public List<SeatResponse> createSeatList(Long hallId, int rows, int columns) {
        Hall hall = validateHall(hallId);
        //TODO: 이미 좌석이 생성되었다면 예외처리
        List<Seat> seats = new ArrayList<>();
        for (char row = 'A'; row < 'A' + rows; row++) {
            for (int col = 1; col <= columns; col++) {
                Seat seat = Seat.builder()
                        .hall(hall)
                        .seatLocation(new SeatLocation(String.valueOf(row), String.valueOf(col)))
                        .build();
                seats.add(seat);
            }
        }

        List<Seat> savedSeats = seatRepository.saveAll(seats);
        return savedSeats.stream()
                .map(SeatResponse::create)
                .collect(Collectors.toList());
    }

    public Map<String, List<SeatSummaryResponse>> getSeatList(Long screeningId, Long hallId) {
        List<Seat> seats = seatRepository.findAllByHall(hallId);
        List<Seat> bookingSeats = bookingSeatRepository.findAllByScreeningIdAndBookingStatusNot(screeningId, BookingStatus.CANCELED);
        Map<String, List<Seat>> sectionSeatMap = seats.stream()
                                                .collect(Collectors.groupingBy(seat -> seat.getSeatLocation().getSection()));

        return sectionSeatMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(seat -> SeatSummaryResponse.create(seat, bookingSeats.contains(seat)))
                                .collect(Collectors.toList())
                ));
    }

    public List<Seat> validateSeats(List<Long> seatIds, Screening screening) {
        List<Seat> seats = seatRepository.findAllByIdIn(seatIds);
        if (seatIds.size() != seats.size()) {
            throw new IllegalArgumentException("유효하지 않은 좌석입니다. 좌석 정보를 다시 확인해 주세요.");
        }

        List<BookingSeat> bookingSeats = bookingSeatRepository.findAllBySeatIdInAndScreening(screening, seatIds);
        for (BookingSeat bookingSeat : bookingSeats) {
            BookingStatus bookingStatus = bookingSeat.getBooking().getBookingStatus();
            if (bookingStatus.equals(BookingStatus.PENDING)) {
                throw new DuplicateSeatBookingException("판매가 진행중인 좌석입니다.");
            } else if (bookingStatus.equals(BookingStatus.CONFIRMED)) {
                throw new DuplicateSeatBookingException("이미 선택된 좌석입니다.");
            }

            if (!bookingStatus.equals(BookingStatus.CANCELED)) {
                throw new IllegalStateException("불가능한 예매입니다.");
            }
        }

        return seats;
    }

    private Hall validateHall(Long hallId) {
        Optional<Hall> optionalHall = hallRepository.findById(hallId);
        if (optionalHall.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 상영관입니다. 상영관 정보를 다시 확인해 주세요.");
        }
        return optionalHall.get();
    }
}
