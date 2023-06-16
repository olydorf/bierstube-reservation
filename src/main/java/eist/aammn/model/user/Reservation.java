package eist.aammn.model.user;

import eist.aammn.ICSBuilder;
import eist.aammn.model.restaurant.RestaurantTable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * A reservation made by a user.
 */

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserR user;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "confirmed", nullable = false)
    private boolean status;

    @Column(name = "amount_guests", nullable = false)
    private int amountGuests;

    @OneToOne
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable restaurantTable;    

    public RestaurantTable getRestaurantTable() {
        return restaurantTable;
    }

    // Default constructor is needed by JPA
    public Reservation() {}

    // You can also create a constructor for easier object creation
    public Reservation(String name, String email, LocalDateTime startTime, LocalDateTime endTime, RestaurantTable restaurantTable, int amountGuests) {
        //this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.restaurantTable = restaurantTable;
        this.amountGuests = amountGuests;
    }

    // Add getter and setter methods for all fields here
    public boolean getStatus(){
        return status;
    }

    public void setStatus(boolean status){
        this.status = true;
    }

    public int getAmountGuests(){
        return amountGuests;
    }

    public UserR getUser(){
        return user;
    }

    public boolean overlapsWith(LocalDateTime time) {
        time = time.plus(1, ChronoUnit.SECONDS);
        return startTime.isBefore(time) && time.isBefore(endTime);
    }

    /**
     * Return a RFC5545 .ics file
     */
    public String toICS() {
        var reminderTime = startTime.minus(1, ChronoUnit.DAYS);
        if (reminderTime.isBefore(LocalDateTime.now()))
            reminderTime = LocalDateTime.now().plus(1, ChronoUnit.MINUTES);
        var startsInDays = reminderTime.until(startTime, ChronoUnit.DAYS);

        return new ICSBuilder()
                .event(startTime, endTime, "Booked table " + restaurantTable.getId() + " at bierstube")
                .alarm(startTime, "Confirm your reservation at  bierstube, booked for in " + startsInDays + " days")
                .build();
    }
}