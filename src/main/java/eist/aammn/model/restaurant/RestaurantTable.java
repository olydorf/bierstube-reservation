package eist.aammn.model.restaurant;

/**
 * A table in a restaurant.
 */
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    // No-argument constructor
    public RestaurantTable() {}

    // Argument constructor
    public RestaurantTable(int tableNumber) {
        this.id= tableNumber;
    }

    // Getters and setters
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }


}

