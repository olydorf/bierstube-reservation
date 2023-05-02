package eist.aammn.model.search;

import eist.aammn.model.Repository;
import eist.aammn.model.restaurant.Cuisine;
import eist.aammn.model.restaurant.PriceCategory;
import eist.aammn.model.restaurant.Restaurant;
import java.util.Optional;
import java.util.Set;

/**
 * Search filters for a restaurant.
 *
 * @see Restaurant
 */
public record Filter(
        Optional<String> name,
        Optional<Double> minRating,
        Optional<PriceCategory> minPrice,
        Optional<PriceCategory> maxPrice,
        Optional<SearchArea> area,
        Set<Cuisine> cuisines) {

    /**
     * Returns true if a restaurant matches this filter.
     */
    public boolean matchesRestaurant(Repository repo, Restaurant r) {
        return this.name.map(name -> match(r.name(), name)).orElse(true)
                && this.minPrice
                        .map(minPrice -> minPrice.compareTo(r.priceCategory()) <= 0)
                        .orElse(true)
                && this.maxPrice
                        .map(maxPrice -> maxPrice.compareTo(r.priceCategory()) >= 0)
                        .orElse(true)
                && this.area
                        .map(area -> area.containsLocation(r.address().location()))
                        .orElse(true)
                && (this.cuisines.isEmpty() || this.cuisines.contains(r.cuisine()))
                && (this.minRating
                        .map(minRating -> repo.averageReviewsForRestaurant(r.id())
                                .map(rating -> rating >= minRating)
                                .orElse(true))
                        .orElse(true));
    }

    /**
     * Returns true if a search for <code>searchTerm</code> should match <code>value</code>.
     */
    private static boolean match(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
