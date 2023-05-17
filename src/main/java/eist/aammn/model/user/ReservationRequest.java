package eist.aammn.model.user;

import eist.aammn.model.restaurant.Restaurant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A reservation request made by a user.
 */
public record ReservationRequest(int restaurant, LocalDateTime startTime, LocalDateTime endTime, int table,User user) {
    static AtomicInteger idCounter = new AtomicInteger(0);

    public Optional<Reservation> toReservation( Restaurant r) {
        var tbl = r.tables().stream().filter(t -> t.tableNumber() == table).findFirst();
        if (tbl.isEmpty())
            return Optional.empty();

        return Optional.of(new Reservation(
                idCounter.getAndIncrement(),
                user,
                r,
                startTime,
                endTime,
                tbl.get()));
    }
}
