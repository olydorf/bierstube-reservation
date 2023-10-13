package eist.aammn.model.user.model;

import eist.aammn.ICSBuilder;
import eist.aammn.model.restaurant.RestaurantTable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javax.persistence.*;

/**
 * A reservation made by a user.
 */
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

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

    @Column(name = "message", nullable = true)
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isStatus() {
        return status;
    }

    public void setAmountGuests(int amountGuests) {
        this.amountGuests = amountGuests;
    }

    public void setRestaurantTable(RestaurantTable restaurantTable) {
        this.restaurantTable = restaurantTable;
    }

    public RestaurantTable getRestaurantTable() {
        return restaurantTable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Default constructor is needed by JPA
    public Reservation() {}

    // You can also create a constructor for easier object creation
    public Reservation(
            String name,
            String email,
            LocalDateTime startTime,
            LocalDateTime endTime,
            RestaurantTable restaurantTable,
            int amountGuests,
            String message) {
        // this.user = user;
        this.name = name;
        this.email = email;
        this.startTime = startTime;
        this.endTime = endTime;
        this.restaurantTable = restaurantTable;
        this.amountGuests = amountGuests;
        this.message = message;
    }

    // Add getter and setter methods for all fields here
    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = true;
    }

    public int getAmountGuests() {
        return amountGuests;
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
