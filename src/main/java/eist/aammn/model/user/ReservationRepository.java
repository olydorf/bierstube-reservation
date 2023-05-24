package eist.aammn.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(LocalDateTime endTime, LocalDateTime startTime);

}