package eist.aammn.model.search;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import eist.aammn.model.restaurant.Location;

/**
 * An area to search restaurants in.
 *
 * @see Filter
 * @see eist.aammn.model.restaurant.Restaurant
 */
public record SearchArea(Location center, double distance) {
    /**
     * Return true if the search area contains the given location.
     */
    public boolean containsLocation(Location l) {
        // calculate the vector from center to l
        double normLong = l.longitude() - center.longitude();
        double normLat = l.latitude() - center.latitude();

        // calculate the length of the vector
        double actualDistance = sqrt(pow(normLong, 2) * pow(normLat, 2));
        return actualDistance <= distance;
    }
}
