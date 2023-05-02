package eist.aammn.model.user;

import eist.aammn.model.restaurant.Restaurant;

/**
 * A review given by a user.
 */
public record Review(Restaurant restaurant, String comment, User author, double rating) {}
