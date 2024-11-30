package mt.movie_theater.api.hall.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import mt.movie_theater.IntegrationTestSupport;
import mt.movie_theater.api.hall.request.HallCreateRequest;
import mt.movie_theater.api.hall.request.HallSeatsCreateRequest;
import mt.movie_theater.api.hall.response.HallResponse;
import mt.movie_theater.api.hall.response.HallSeatsResponse;
import mt.movie_theater.domain.hall.Hall;
import mt.movie_theater.domain.hall.HallRepository;
import mt.movie_theater.domain.movie.ScreeningType;
import mt.movie_theater.domain.theater.Region;
import mt.movie_theater.domain.theater.Theater;
import mt.movie_theater.domain.theater.TheaterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class HallServiceTest extends IntegrationTestSupport {
    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private HallService hallService;
    @Autowired
    private HallRepository hallRepository;

    @DisplayName("신규 상영관을 등록한다.")
    @Test
    void createHallTest() {
        //given
        Theater theater = createTheater();
        HallCreateRequest request = HallCreateRequest.builder()
                                    .theaterId(theater.getId())
                                    .name("1관")
                                    .screeningType(ScreeningType.IMAX)
                                    .hallTypeModifier(3000)
                                    .build();
        //when
        HallResponse response = hallService.createHall(request);

        //then
        assertThat(response.getId()).isNotNull();
        assertThat(response)
                .extracting("name", "totalSeats", "screeningType", "hallTypeModifier")
                .contains("1관", 0, "IMAX", 3000);
        assertThat(response.getTheater().getId()).isEqualTo(theater.getId());
    }

    @DisplayName("신규 상영관을 등록할 때, 유효하지 않은 영화관일 경우 예외가 발생한다.")
    @Test
    void createHallWithNoTheater() {
        //given
        HallCreateRequest request = HallCreateRequest.builder()
                .theaterId(1L)
                .name("1관")
                .screeningType(ScreeningType.TWO_D)
                .build();

        //when, then
        assertThatThrownBy(() -> hallService.createHall(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 영화관입니다. 영화관 정보를 다시 확인해 주세요.");
    }

    @DisplayName("좌석리스트와 함께 상영관을 등록한다.")
    @Test
    void createHallWithSeats() {
        //given
        Theater theater = createTheater();
        HallSeatsCreateRequest request = HallSeatsCreateRequest.builder()
                            .theaterId(theater.getId())
                            .name("1관")
                            .screeningType(ScreeningType.TWO_D)
                            .hallTypeModifier(3000)
                            .rows(2)
                            .columns(2)
                            .build();

        //when
        HallSeatsResponse response = hallService.createHallWithSeats(request);

        //then
        assertThat(response.getHallId()).isNotNull();
        assertThat(response.getSeats()).hasSize(4)
                .extracting("section", "seatRow")
                .containsExactlyInAnyOrder(
                        tuple("A", "1"),
                        tuple("A", "2"),
                        tuple("B", "1"),
                        tuple("B", "2")
                );
    }

    @DisplayName("좌석리스트와 함께 상영관을 등록할 때, 유효하지 않은 영화관일 경우 예외가 발생한다.")
    @Test
    void createHallWithSeatsWithNoTheater() {
        //given
        HallSeatsCreateRequest request = HallSeatsCreateRequest.builder()
                .theaterId(1L)
                .name("1관")
                .screeningType(ScreeningType.TWO_D)
                .hallTypeModifier(3000)
                .rows(2)
                .columns(2)
                .build();

        //when, then
        assertThatThrownBy(() -> hallService.createHallWithSeats(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 영화관입니다. 영화관 정보를 다시 확인해 주세요.");
    }

    @DisplayName("영화관에 해당하는 상영관 리스트를 조회한다.")
    @Test
    void getHalls() {
        //given
        Theater theater1 = createTheater();
        Theater theater2 = createTheater();

        createHall(theater1, "1관", ScreeningType.TWO_D, 1000);
        createHall(theater1, "2관", ScreeningType.IMAX, 2000);
        createHall(theater2, "3관", ScreeningType.FOUR_DX, 3000);

        //when
        List<HallResponse> halls = hallService.getHalls(theater1.getId());

        //then
        assertThat(halls).hasSize(2)
                .extracting("name", "totalSeats", "screeningType", "hallTypeModifier")
                .containsExactlyInAnyOrder(
                        tuple( "1관", 0, "2D", 1000),
                        tuple("2관", 0, "IMAX", 2000)
                );
    }

    private Theater createTheater() {
        Theater theater = Theater.builder()
                .region(Region.SEOUL)
                .build();
        return theaterRepository.save(theater);
    }
    private Hall createHall(Theater theater, String name, ScreeningType screeningType, int hallTypeModifier) {
        Hall hall = Hall.builder()
                .theater(theater)
                .name(name)
                .screeningType(screeningType)
                .hallTypeModifier(hallTypeModifier)
                .build();
        return hallRepository.save(hall);
    }
}