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

    @Column(name = "capacity")
    private int capacity;



    public RestaurantTable(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;

    }

    public RestaurantTable(int id) {
    }


    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    // No-argument constructor
    public RestaurantTable() {}


    // Getters and setters
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }


}

