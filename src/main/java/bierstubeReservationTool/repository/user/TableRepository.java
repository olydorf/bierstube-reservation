package bierstubeReservationTool.repository.user;

import bierstubeReservationTool.model.restaurant.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<RestaurantTable,Integer> {
}
