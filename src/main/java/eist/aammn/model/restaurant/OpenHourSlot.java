package eist.aammn.model.restaurant;

import java.time.*;

/**
 * A single time slot in the opening hours of a restaurant.
 */
public record OpenHourSlot(DayOfWeek weekDay, LocalTime startTime, LocalTime endTime) {
    public boolean containsTime(LocalDateTime time) {
        if (!time.getDayOfWeek().equals(this.weekDay)) {
            return false;
        }

        // check that startTime < time < endTime ignoring dates
        return startTime.isBefore(time.toLocalTime()) && time.toLocalTime().isBefore(endTime);
    }
}
