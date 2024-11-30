package mt.movie_theater.domain.hall;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallRepository extends JpaRepository<Hall, Long> {
    List<Hall> findAllByTheaterId(Long theaterId);
}
