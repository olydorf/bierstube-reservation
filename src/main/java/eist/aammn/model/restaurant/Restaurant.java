package eist.aammn.model.restaurant;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A restaurant.
 */
public record Restaurant(
        int id,
        String name,
        String description,
        List<URL> pictures,
        Cuisine cuisine,
        PriceCategory priceCategory,
        List<OpenHourSlot> openingHours,
        URL website,
        Address address,
        List<Table> tables,
        String layoutSvg) {

    /**
     * Return true if the restaurant is open at the given date and time.
     */
    public boolean isOpenAt(LocalDateTime time) {
        return openingHours.stream().anyMatch(s -> s.containsTime(time));
    }
}
