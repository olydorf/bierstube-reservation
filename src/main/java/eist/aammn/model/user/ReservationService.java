package eist.aammn.model.user;


import eist.aammn.model.restaurant.Restaurant;
import eist.aammn.model.restaurant.RestaurantTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    private Restaurant restaurant;
    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Integer id) {
        return reservationRepository.findById(id);
    }

    public Reservation createReservation(UserR user, LocalDateTime startTime, LocalDateTime endTime, RestaurantTable table) {
        Set<RestaurantTable> freeTables = getFreeTables(startTime);
        boolean free = freeTables.stream()
                .anyMatch(t -> t.getId() == table.getId());
        if (!free) {
            throw new IllegalStateException("Table is already reserved at the specified time");
        }

        Reservation reservation = new Reservation(user, startTime, endTime, table);
                 return reservationRepository.save(reservation);
    }

    public Set<RestaurantTable> getFreeTables(LocalDateTime time) {
        List<Reservation> reservations = getAllReservations();
        return Restaurant.getTables().stream()
                .filter(t -> reservations.stream()
                        .noneMatch(rsv->rsv.overlapsWith(time) && rsv.getRestaurantTable() == t))
                .collect(Collectors.toSet());
    }

    public void deleteReservation(Integer id) {
        reservationRepository.deleteById(id);
    }
}

