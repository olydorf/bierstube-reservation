package eist.aammn.model.user;

import eist.aammn.ICSBuilder;
import eist.aammn.model.restaurant.Restaurant;
import eist.aammn.model.restaurant.Table;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * A reservation made by a user.
 */
public record Reservation(
        int id,
        User user,
        Restaurant restaurant,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Table table) {

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
                .event(startTime, endTime, "Booked table " + table.tableNumber() + " at " + restaurant.name())
                .alarm(startTime, "Confirm your reservation at " + restaurant.name() + " booked for in " + startsInDays + " days")
                .build();
    }
}
