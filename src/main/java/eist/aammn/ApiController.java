package eist.aammn;

import eist.aammn.model.Repository;
import eist.aammn.model.restaurant.*;
import eist.aammn.model.search.Filter;
import eist.aammn.model.search.SearchArea;
import eist.aammn.model.user.Reservation;
import eist.aammn.model.user.ReservationRequest;
import eist.aammn.model.user.Review;
import eist.aammn.model.user.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Controller
@ResponseBody
@RequestMapping("/api/")
public class ApiController {
    static Repository globalRepository = Repository.newExampleRepo();

    private final Repository repo;
    private final User currentUser;

    public ApiController() {
        this.repo = Repository.newExampleRepo();
        this.currentUser = new User("AAMMN".hashCode(), "Me", "me@example.org", "+49 153 1234 5678");
        this.repo.addUser(currentUser);
    }

    public ApiController(Repository repo, User currentUser) {
        this.repo = repo;
        this.currentUser = currentUser;
    }

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @GetMapping(path = "")
    public ResponseEntity<List<String>> listEndpoints() {
        return ResponseEntity.ok(requestMappingHandlerMapping.getHandlerMethods().keySet().stream()
                .filter(e -> e.getActivePatternsCondition().toString().startsWith("[/api"))
                .map(e -> {
                    var s = e.getActivePatternsCondition().toString();
                    return s.substring(1, s.length() - 1);
                })
                .collect(Collectors.toList()));
    }

    @GetMapping("cuisines")
    public Cuisine[] cuisines() {
        return Cuisine.values();
    }

    @GetMapping("restaurants")
    public ResponseEntity<Set<Restaurant>> restaurants(
            @RequestParam(required = false) @Nullable String name,
            @RequestParam(required = false) Optional<Double> minRating,
            @RequestParam(required = false) Optional<PriceCategory> minPrice,
            @RequestParam(required = false) Optional<PriceCategory> maxPrice,
            @RequestParam(required = false) Optional<Double> lng,
            @RequestParam(required = false) Optional<Double> lat,
            @RequestParam(required = false) Optional<Double> distance,
            @RequestParam(required = false) @Nullable Set<Cuisine> cuisines) {

        Optional<SearchArea> area = Optional.empty();
        if (lng.isPresent() || lat.isPresent() || distance.isPresent()) {
            if (lng.isEmpty() || lat.isEmpty() || distance.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            area = Optional.of(new SearchArea(new Location(lng.get(), lat.get()), distance.get()));
        }

        return ResponseEntity.ok(repo.getRestaurants(new Filter(
                Optional.ofNullable(name),
                minRating,
                minPrice,
                maxPrice,
                area,
                Optional.ofNullable(cuisines).orElse(Set.of()))));
    }

    @GetMapping("restaurants/{id}")
    public ResponseEntity<Restaurant> restaurant(@PathVariable int id) {
        return repo.getRestaurant(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("restaurants/{id}/reviews")
    public ResponseEntity<Set<Review>> restaurantReviews(@PathVariable int id) {
        return ResponseEntity.ok(repo.getReviewsForRestaurant(id));
    }

    @GetMapping("restaurants/{id}/reviews/avg")
    public ResponseEntity<Double> restaurantAverageReviews(@PathVariable int id) {
        return ResponseEntity.ok(repo.averageReviewsForRestaurant(id).orElse(-1.0));
    }

    @GetMapping("restaurants/{id}/tables/freeAt/{time}")
    public ResponseEntity<Set<Integer>> tablesFreeAt(@PathVariable int id, @PathVariable("time") String timeStr) {
        LocalDateTime time = LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_DATE_TIME);
        return repo.getRestaurant(id)
                .map(r -> repo.getFreeTables(r, time).stream()
                        .map(Table::tableNumber)
                        .collect(Collectors.toSet()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("restaurants/{id}/reservations")
    public ResponseEntity<Set<Reservation>> reservationsForRestaurant(@PathVariable int id) {
        return ResponseEntity.ok(repo.getReservationsByRestaurant(id));
    }

    @GetMapping("reservations")
    public ResponseEntity<Set<Reservation>> reservations() {
        return ResponseEntity.ok(repo.getReservationsByUser(currentUser.id()));
    }

    @GetMapping("reservations/{id}")
    public ResponseEntity<Reservation> reservation(@PathVariable int id) {
        return repo.getReservation(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "reservations/{id}/calendar.ics", produces = ICSBuilder.MIME_TYPE)
    public ResponseEntity<String> reservationCalendar(@PathVariable int id) {
        return repo.getReservation(id)
                .map(Reservation::toICS)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(path = "reservations")
    public ResponseEntity<Reservation> addReservation(@RequestBody ReservationRequest req) {
        return repo.getRestaurant(req.restaurant())
                .flatMap(r -> req.toReservation(currentUser, r))
                .filter(repo::addReservation)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping(path = "reservations/{id}/confirmed")
    public ResponseEntity<Void> confirmReservation(@PathVariable int id, @RequestBody boolean confirm) {
        repo.setReservationConfirmed(id, confirm);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "reservations/{id}/confirmed")
    public ResponseEntity<Boolean> reservationConfirmed(@PathVariable int id) {
        return ResponseEntity.ok(repo.isReservationConfirmed(id));
    }
}
