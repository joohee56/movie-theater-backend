package mt.movie_theater.domain.hall;

import static org.assertj.core.groups.Tuple.tuple;

import jakarta.transaction.Transactional;
import java.util.List;
import mt.movie_theater.IntegrationTestSupport;
import mt.movie_theater.domain.theater.Theater;
import mt.movie_theater.domain.theater.TheaterRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
class HallRepositoryTest extends IntegrationTestSupport {
    @Autowired
    private HallRepository hallRepository;
    @Autowired
    private TheaterRepository theaterRepository;

    @DisplayName("영화관에 해당하는 상영관 리스트를 조회한다.")
    @Test
    void findAllByTheaterId() {
        //given
        Theater theater1 = createTheater("고양스타필드");
        Theater theater2 = createTheater("광명ak플라자");
        Hall hall1 = createHall(theater1, "1관");
        Hall hall2 = createHall(theater1, "2관");
        Hall hall3 = createHall(theater2, "3관");

        //when
        List<Hall> halls = hallRepository.findAllByTheaterId(theater1.getId());

        //then
        Assertions.assertThat(halls).hasSize(2)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple(hall1.getId(), "1관"),
                        tuple(hall2.getId(), "2관"));
    }

    private Theater createTheater(String name) {
        Theater theater = Theater.builder()
                .name(name)
                .build();
        return theaterRepository.save(theater);
    }

    private Hall createHall(Theater theater, String name) {
        Hall hall = Hall.builder()
                .theater(theater)
                .name(name)
                .build();
        return hallRepository.save(hall);
    }

}