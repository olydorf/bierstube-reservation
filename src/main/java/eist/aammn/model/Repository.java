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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Repository {
    private final HashMap<Integer, Restaurant> restaurant = new HashMap<>();
    private final HashMap<Integer, User> users = new HashMap<>();
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
     * Add a new user.
     *
     * @throws IllegalArgumentException if a user with the same id already exists
     */
    public synchronized void addUser(User u) {
        var existing = users.get(u.id());
        if (existing == null) users.put(u.id(), u);
        else if (!existing.equals(u))
            throw new IllegalArgumentException("tried adding a different user with a duplicate id");
    }

    /**
     * Return the user with the given id.
     */
    public synchronized Optional<User> getUser(int id) {
        return Optional.ofNullable(users.get(id));
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
        var byUser = reservationsByUser.computeIfAbsent(r.user().id(), id -> new HashSet<>());
        var byRestaurant =
                reservationsByRestaurant.computeIfAbsent(r.restaurant().id(), id -> new HashSet<>());
        var existing = reservations.get(r.id());
        // check whether tables are still free
        if (existing == null) {
            if (!getFreeTables(r.restaurant(), r.startTime()).contains(r.table())) {
                return false;
            }

            reservations.put(r.id(), r);
            byUser.add(r);
            byRestaurant.add(r);
            return true;
        } else if (!existing.equals(r))
            throw new IllegalArgumentException("tried adding a different user with a duplicate id");
        return true;
    }

    /**
     * Remove a reservation with the given id.
     */
    public synchronized void removeReservation(int r) {
        Reservation prev = reservations.remove(r);
        if (prev == null) return;

        reservationsByUser.getOrDefault(prev.user().id(), new HashSet<>()).remove(prev);
        reservationsByRestaurant
                .getOrDefault(prev.restaurant().id(), new HashSet<>())
                .remove(prev);
        confirmedReservations.remove(r);
    }

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

            List<OpenHourSlot> ohNordsee = new ArrayList<>();
            for (DayOfWeek d : DayOfWeek.values()) {
                if (d == DayOfWeek.SUNDAY) ohNordsee.add(new OpenHourSlot(d, LocalTime.of(11, 0), LocalTime.of(17, 0)));
                else ohNordsee.add(new OpenHourSlot(d, LocalTime.of(8, 30), LocalTime.of(20, 0)));
            }

            List<OpenHourSlot> ohBluenile = new ArrayList<>();
            for (DayOfWeek d : DayOfWeek.values()) {
                if (d == DayOfWeek.SATURDAY)
                    ohBluenile.add(new OpenHourSlot(d, LocalTime.of(18, 0), LocalTime.of(23, 0)));
                else ohBluenile.add(new OpenHourSlot(d, LocalTime.of(18, 0), LocalTime.of(23, 59)));
            }

            List<OpenHourSlot> ohTantris = new ArrayList<>();
            for (DayOfWeek d : DayOfWeek.values()) {
                if (d == DayOfWeek.WEDNESDAY
                        || d == DayOfWeek.THURSDAY
                        || d == DayOfWeek.FRIDAY
                        || d == DayOfWeek.SUNDAY) {
                    ohTantris.add(new OpenHourSlot(d, LocalTime.of(12, 0), LocalTime.of(16, 0)));
                    ohTantris.add(new OpenHourSlot(d, LocalTime.of(18, 30), LocalTime.of(23, 59)));
                }
            }

            List<Table> nordseeTables =
                    IntStream.range(1, 15).mapToObj(Table::new).toList();
            String nordseeSVG = ResourceLoader.mustLoadString("/static/nordsee-tables.svg");

            List<Table> fancyTables =
                    IntStream.range(1, 11).mapToObj(Table::new).toList();
            String fancySVG = ResourceLoader.mustLoadString("/static/fancy-tables.svg");
            Restaurant nordsee = new Restaurant(
                    ++id,
                    "Nordsee",
                    "NORDSEE covers the entire spectrum of fish products.",
                    List.of(
                            new URL(
                                    "https://www.nordsee.com/fileadmin/_processed_/d/5/csm_filiale-muenchen-olympia-einkaufs-zentrum-riesstrasse-nordsee-1_36edd21c02.jpg")),
                    Cuisine.Fish,
                    PriceCategory.Cheap,
                    ohNordsee,
                    new URL(
                            "https://www.nordsee.com/en/restaurants/detail/store/nordsee-muenchen-viktualienmarkt-249/"),
                    new Address("Viktualienmarkt", 0, 80331, new Location(0, 0)),
                    nordseeTables,
                    nordseeSVG);
            addRestaurant(nordsee);
            Restaurant bluenile = new Restaurant(
                    ++id,
                    "Blue Nile",
                    "Unser Restaurant widmet sich ganz den ostafrikantischen Spezialitäten.",
                    List.of(
                            new URL(
                                    "https://media-cdn.tripadvisor.com/media/photo-s/0e/ee/d0/1a/menu-mit-diversen-gerichten.jpg")),
                    Cuisine.Ethiopian,
                    PriceCategory.Normal,
                    ohBluenile,
                    new URL("https://bluenile2.eatbu.com/?lang=de"),
                    new Address("Viktor-Scheffel-Straße", 22, 80803, new Location(0, 0)),
                    fancyTables,
                    fancySVG);
            addRestaurant(bluenile);
            Restaurant tantris = new Restaurant(
                    ++id,
                    "Tantris",
                    "Craftsmanship, great culinary art and lived hospitality come together at Tantris Maison Culinaire to create the ultimate sensory experience.",
                    List.of(
                            new URL(
                                    "https://axwwgrkdco.cloudimg.io/v7/__gmpics__/f7410416c19b47d098d0d8556a025b32?width=1000")),
                    Cuisine.French,
                    PriceCategory.Expensive,
                    ohTantris,
                    new URL("https://tantris.de/en/restaurant-tantris/"),
                    new Address("Johann–Fichte–Straße", 7, 80805, new Location(0, 0)),
                    fancyTables,
                    fancySVG);
            addRestaurant(tantris);

            User aziz = new User(++id, "Aziz", "aziz@example.com", randomPhoneNumber(r));
            addUser(aziz);
            User markus = new User(++id, "Markus", "markus@example.com", randomPhoneNumber(r));
            addUser(markus);
            User nabil = new User(++id, "Nabil", "nabil@example.com", randomPhoneNumber(r));
            addUser(nabil);

            addReview(new Review(nordsee, "Tacos waren lecker.", aziz, 5.));
            addReview(new Review(nordsee, "Absolutely disgusting.", markus, 3.));
            addReview(new Review(nordsee, "Delicious but a baby portion.", nabil, 3.));
            addReview(new Review(bluenile, "Heaven on earth.", markus, 5.));
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new RuntimeException("unreachable", e);
        }
    }

    private String randomPhoneNumber(Random r) {
        return String.format("+49 %d%d %04d %04d", 15 + r.nextInt(3), r.nextInt(9), r.nextInt(9999), r.nextInt(9999));
    }
}
