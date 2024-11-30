package mt.movie_theater.domain.theater;

import java.util.List;
import mt.movie_theater.domain.theater.dto.RegionTheaterCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {

    List<Theater> findALlByRegion(@Param("region") Region region);

    @Query("select new mt.movie_theater.domain.theater.dto.RegionTheaterCountDto(t.region, count(t)) from Theater t"
            + " group by t.region")
    List<RegionTheaterCountDto> findRegionListWithTheaterCount();
}
