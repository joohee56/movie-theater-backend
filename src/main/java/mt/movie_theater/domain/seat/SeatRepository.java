package mt.movie_theater.domain.seat;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query("select s from Seat s where s.hall.id= :hallId")
    List<Seat> findAllByHall(@Param("hallId") Long hallId);

    List<Seat> findAllByIdIn(List<Long> ids);
}
