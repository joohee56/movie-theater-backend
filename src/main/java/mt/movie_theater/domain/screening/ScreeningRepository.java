package mt.movie_theater.domain.screening;

import java.time.LocalDateTime;
import java.util.List;
import mt.movie_theater.domain.movie.Movie;
import mt.movie_theater.domain.screening.dto.RegionScreeningCountDto;
import mt.movie_theater.domain.screening.dto.TheaterScreeningCountDto;
import mt.movie_theater.domain.theater.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    @Query("select s.movie from Screening s "
            + "where s.startTime >= :startDateTime and s.startTime < :endDateTime "
            + "and (:theaterId is null or s.hall.theater.id= :theaterId) "
            + "group by s.movie")
    List<Movie> findMoviesByDateAndOptionalTheaterId(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, @Param("theaterId") Long theaterId);

    @Query("select new mt.movie_theater.domain.screening.dto.RegionScreeningCountDto(s.hall.theater.region, count(s)) from Screening s "
            + "where s.startTime >= :startDateTime and s.startTime < :endDateTime "
            + "and (:movieId is null or s.movie.id = :movieId) "
            + "group by s.hall.theater.region")
    List<RegionScreeningCountDto> countScreeningByRegion(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, @Param("movieId") Long movieId);

    @Query("select new mt.movie_theater.domain.screening.dto.TheaterScreeningCountDto(s.hall.theater, count(s)) from Screening s "
            + "where s.hall.theater.region= :region "
            + "and s.startTime >= :startTime and s.startTime < :endTime "
            + "and (:movieId is null or s.movie.id= :movieId) "
            + "group by s.hall.theater")
    List<TheaterScreeningCountDto> findTheaterScreeningCounts(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("region") Region region, @Param("movieId") Long movieId);

    @Query("select s from Screening s "
            + "where s.startTime >= :startDateTime and s.startTime < :endDateTime "
            + "and (:movieId is null or s.movie.id= :movieId) "
            + "and s.hall.theater.id= :theaterId "
            + "order by s.startTime asc")
    List<Screening> findAllByDateAndTheaterIdAndOptionalMovieId(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, @Param("movieId") Long movieId, @Param("theaterId") Long theaterId);
}
