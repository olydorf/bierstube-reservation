package eist.aammn.model.user;

import eist.aammn.model.restaurant.RestaurantTable;

import java.time.LocalDateTime;

public class ReservationRequestDTO {
    private UserR user;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private RestaurantTable restaurantTable;

    public ReservationRequestDTO() {
        // Default constructor is needed for deserialization
    }

    public ReservationRequestDTO(UserR user, LocalDateTime startTime, LocalDateTime endTime, RestaurantTable table) {
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.restaurantTable = table;
    }

    public UserR getUser() {
        return user;
    }

    public void setUser(UserR user) {
        this.user = user;
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

