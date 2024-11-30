package mt.movie_theater.api.theater.service;

import static mt.movie_theater.domain.theater.Region.GYEONGGI;
import static mt.movie_theater.domain.theater.Region.JEJU;
import static mt.movie_theater.domain.theater.Region.SEOUL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import jakarta.transaction.Transactional;
import java.util.List;
import mt.movie_theater.IntegrationTestSupport;
import mt.movie_theater.api.theater.request.TheaterCreateRequest;
import mt.movie_theater.api.theater.response.RegionTheaterCountResponse;
import mt.movie_theater.api.theater.response.TheaterIdNameResponse;
import mt.movie_theater.api.theater.response.TheaterResponse;
import mt.movie_theater.domain.theater.Region;
import mt.movie_theater.domain.theater.Theater;
import mt.movie_theater.domain.theater.TheaterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
class TheaterServiceTest extends IntegrationTestSupport {
    @Autowired
    private TheaterService theaterService;
    @Autowired
    private TheaterRepository theaterRepository;

    @DisplayName("영화관을 등록한다.")
    @Test
    void createTheaterTest() {
        //given
        TheaterCreateRequest request = TheaterCreateRequest.builder()
                .name("강남")
                .address("서울시 강남구")
                .region(SEOUL)
                .latitude(100F)
                .longitude(100F)
                .contactNumber("1577-1577")
                .build();

        //when
        TheaterResponse response = theaterService.createTheater(request);

        //then
        assertThat(response.getId()).isNotNull();
        assertThat(response)
                .extracting("name", "address", "region", "latitude", "longitude", "contactNumber")
                .containsExactly("강남", "서울시 강남구", "서울", 100F, 100F, "1577-1577");
    }

    @DisplayName("지역리스트와 각 지역에 속한 영화관 갯수를 조회한다.")
    @Test
    void getRegionsWithTheaterCount() {
        //given
        createTheater(SEOUL, "강남");
        createTheater(SEOUL, "강동");
        createTheater(GYEONGGI, "고양스타필드");

        //when
        List<RegionTheaterCountResponse> response = theaterService.getRegionsWithTheaterCount();

        //then
        assertThat(response).hasSize(2)
                .extracting("region", "regionDisplay", "count")
                .containsExactlyInAnyOrder(
                        tuple("SEOUL", "서울", 2L),
                        tuple("GYEONGGI", "경기", 1L)
                );
    }

    @DisplayName("지역에 해당하는 영화관 리스트를 조회한다.")
    @Test
    void getTheatersByRegion() {
        //given
        Region targetRegion = SEOUL;
        Theater theater1 = createTheater(SEOUL, "강남");
        Theater theater2 = createTheater(SEOUL, "강동");
        createTheater(JEJU, "제주삼화");

        //when
        List<TheaterIdNameResponse> response = theaterService.getTheatersByRegion(targetRegion);

        //then
        assertThat(response).hasSize(2)
                .extracting("theaterId", "theaterName")
                .containsExactlyInAnyOrder(
                        tuple(theater1.getId(), "강남"),
                        tuple(theater2.getId(), "강동")
                );
    }

    private Theater createTheater(Region region, String name) {
        Theater theater = Theater.builder()
                .region(region)
                .name(name)
                .build();
        return theaterRepository.save(theater);
    }
}