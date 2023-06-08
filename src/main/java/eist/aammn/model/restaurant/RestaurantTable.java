package eist.aammn.model.restaurant;

/**
 * A table in a restaurant.
 */
import javax.persistence.*;

@Entity
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "table_number")
    private int id;


    // No-argument constructor
    public RestaurantTable() {}

    // Argument constructor
    public RestaurantTable(int id) {
        this.id= id;
    }

    // Getters and setters
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }


}

