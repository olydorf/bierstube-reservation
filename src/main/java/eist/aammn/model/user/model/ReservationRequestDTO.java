package eist.aammn.model.user.model;

import eist.aammn.model.restaurant.RestaurantTable;

import java.time.LocalDateTime;

public class ReservationRequestDTO {
    private String name;
    private String email;
    private int amountGuests;
    private boolean status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private RestaurantTable restaurantTable;

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

    public int getAmountGuests() {
        return amountGuests;
    }

    public void setAmountGuests(int amountGuests) {
        this.amountGuests = amountGuests;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ReservationRequestDTO() {
        // Default constructor is needed for deserialization
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

    public RestaurantTable getRestaurantTable() {
        return restaurantTable;
    }

    public void setRestaurantTable(RestaurantTable table) {
        this.restaurantTable = table;
    }
}

