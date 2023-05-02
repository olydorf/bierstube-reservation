package eist.aammn.model.restaurant;

/**
 * Address of a restaurant.
 *
 * @see Restaurant
 */
public record Address(String streetName, int streetNumber, int postCode, Location location) {}
