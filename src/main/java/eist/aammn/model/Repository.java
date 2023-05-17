package eist.aammn.model;

import eist.aammn.ResourceLoader;
import eist.aammn.model.restaurant.*;
import eist.aammn.model.search.Filter;
import eist.aammn.model.user.Reservation;
import eist.aammn.model.user.Review;
import eist.aammn.model.user.User;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Repository {
    private final HashMap<Integer, Restaurant> restaurant = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger();
    private final HashMap<Integer, HashSet<Review>> reviewsByRestaurant = new HashMap<>();

    private final HashMap<Integer, Reservation> reservations = new HashMap<>();
    private final HashMap<Integer, HashSet<Reservation>> reservationsByUser = new HashMap<>();
    private final HashMap<Integer, HashSet<Reservation>> reservationsByRestaurant = new HashMap<>();
    private final HashSet<Integer> confirmedReservations = new HashSet<>();

    /**
     * Add a new restaurant.
     *
     * @throws IllegalArgumentException if a restaurant with the same id already exists
     */
    public synchronized void addRestaurant(Restaurant r) {
        var existing = restaurant.get(r.id());
        if (existing == null) restaurant.put(r.id(), r);
        else if (!existing.equals(r))
            throw new IllegalArgumentException("tried adding a different restaurant with a duplicate id");
    }

    /**
     * Return the restaurant with the given id.
     */
    public synchronized Optional<Restaurant> getRestaurant(int id) {
        return Optional.ofNullable(restaurant.get(id));
    }

    /**
     * Return all restaurants.
     */
    public synchronized Set<Restaurant> getRestaurants() {
        return Set.copyOf(new HashSet<>(restaurant.values()));
    }

    /**
     * Return all restaurants.
     */
    public synchronized Set<Restaurant> getRestaurants(Filter f) {
        return restaurant.values().stream()
                .filter(r -> f.matchesRestaurant(this, r))
                .collect(Collectors.toSet());
    }


    /**
     * Add a review.
     */
    public synchronized void addReview(Review r) {
        var set = reviewsByRestaurant.computeIfAbsent(r.restaurant().id(), id -> new HashSet<>());
        var existing = set.stream().filter(e -> e.equals(r)).findAny();
        if (existing.isEmpty()) set.add(r);
    }

    /**
     * Returns all reviews for a restaurant with the given id..
     */
    public synchronized Set<Review> getReviewsForRestaurant(int id) {
        return Set.copyOf(reviewsByRestaurant.getOrDefault(id, new HashSet<>()));
    }

    /**
     * Get the average reviews for a restaurant with the given id.
     */
    public synchronized Optional<Double> averageReviewsForRestaurant(int id) {
        var avg =
                getReviewsForRestaurant(id).stream().mapToDouble(Review::rating).average();
        if (avg.isPresent()) return Optional.of(avg.getAsDouble());
        else return Optional.empty();
    }

    /**
     * Add a reservation.
     *
     * @return true if successful
     * @throws IllegalArgumentException if a user with the same id already exists
     */
    public synchronized boolean addReservation(Reservation r) {
        var byRestaurant =
                reservationsByRestaurant.computeIfAbsent(r.restaurant().id(), id -> new HashSet<>());
        var existing = reservations.get(r.id());
        // check whether tables are still free
        if (existing == null) {
            if (!getFreeTables(r.restaurant(), r.startTime()).contains(r.table())) {
                return false;
            }

            reservations.put(r.id(), r);
            byRestaurant.add(r);
            return true;
        } else if (!existing.equals(r))
            throw new IllegalArgumentException("tried adding a different user with a duplicate id");
        return true;
    }

    /**
     * Remove a reservation with the given id.

    public synchronized void removeReservation(int r) {
        Reservation prev = reservations.remove(r);
        if (prev == null) return;

        reservationsByUser.getOrDefault(prev.user().getId(), new HashSet<>()).remove(prev);
        reservationsByRestaurant
                .getOrDefault(prev.restaurant().id(), new HashSet<>())
                .remove(prev);
        confirmedReservations.remove(r);
    }*/

    /**
     * Confirm the reservation with the given id.
     */
    public synchronized void setReservationConfirmed(int r, boolean confirm) {
        if (confirm) {
            if (reservations.containsKey(r)) confirmedReservations.add(r);
        } else confirmedReservations.remove(r);
    }

    /**
     * Returns true if reservation with the given id is confirmed.
     */
    public synchronized boolean isReservationConfirmed(int r) {
        return confirmedReservations.contains(r);
    }

    /**
     * Get all reservations by a user with the given id.
     */
    public synchronized Set<Reservation> getReservationsByUser(int id) {
        return Set.copyOf(reservationsByUser.getOrDefault(id, new HashSet<>()));
    }

    /**
     * Get all reservations for a restaurant with the given id.
     */
    public synchronized Set<Reservation> getReservationsByRestaurant(int id) {
        return Set.copyOf(reservationsByRestaurant.getOrDefault(id, new HashSet<>()));
    }

    /**
     * Get the reservation with the given id.
     */
    public synchronized Optional<Reservation> getReservation(int id) {
        return Optional.ofNullable(reservations.get(id));
    }

    public Set<Table> getFreeTables(Restaurant r, LocalDateTime time) {
        return r.tables().stream()
                .filter(t -> getReservationsByRestaurant(r.id()).stream()
                        .noneMatch(rsv -> rsv.overlapsWith(time) && rsv.table() == t))
                .collect(Collectors.toSet());
    }

    public static Repository newExampleRepo() {
        var r = new Repository();
        r.addExampleData();
        return r;
    }

    public void addExampleData() {
        try {
            int id = 0;
            Random r = new Random("AAMMN".hashCode());



            List<OpenHourSlot> ohTantris = new ArrayList<>();
            for (DayOfWeek d : DayOfWeek.values()) {
                if (    d == DayOfWeek.MONDAY
                        || d == DayOfWeek.TUESDAY
                        || d == DayOfWeek.WEDNESDAY
                        || d == DayOfWeek.THURSDAY
                        || d == DayOfWeek.FRIDAY
                        || d == DayOfWeek.SATURDAY) {
                    ohTantris.add(new OpenHourSlot(d, LocalTime.of(19, 0), LocalTime.of(23, 30)));
                }
            }


            List<Table> fancyTables =
                    IntStream.range(1, 11).mapToObj(Table::new).toList();
            String fancySVG = ResourceLoader.mustLoadString("/static/fancy-tables.svg");

            Restaurant bierstube = new Restaurant(
                    ++id,
                    "Bierstube",
                    "Craftsmanship, great culinary art and lived hospitality come together at Tantris Maison Culinaire to create the ultimate sensory experience.",
                    List.of(
                            new URL(
                                    "https://axwwgrkdco.cloudimg.io/v7/__gmpics__/f7410416c19b47d098d0d8556a025b32?width=1000")),
                    Cuisine.French,
                    PriceCategory.Expensive,
                    ohTantris,
                    new URL("https://www.oly-dorf.de/betriebe/bierstube-ueberblick/"),
                    new Address("Helene-Mayer-Ring", 9, 80809, new Location(0, 0)),
                    fancyTables,
                    fancySVG);
            addRestaurant(bierstube);



        } catch (MalformedURLException | FileNotFoundException e) {
            throw new RuntimeException("unreachable", e);
        }
    }

    private String randomPhoneNumber(Random r) {
        return String.format("+49 %d%d %04d %04d", 15 + r.nextInt(3), r.nextInt(9), r.nextInt(9999), r.nextInt(9999));
    }
}
