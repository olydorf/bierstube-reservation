package eist.aammn.model.user;

import eist.aammn.model.restaurant.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<RestaurantTable,Integer> {
}
