package mt.movie_theater.domain.booking;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select distinct b from Booking b"
            + " join fetch b.bookingSeats"
            + " where b.user.id= :userId"
            + " order by b.updatedAt desc")
    List<Booking> findAllWithBookingSeatsByUserId(@Param("userId") Long userId);

    @Query("select distinct b from Booking b"
            + " join fetch b.bookingSeats"
            + " where b.id= :bookingId")
    Optional<Booking> findByIdWithBookingSeats(@Param("bookingId") Long bookingId);
}
