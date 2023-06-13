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
    private  final TableRepository tableRepository;

    private  final UserRRepository userRRepository;


    @Autowired
    private Restaurant restaurant;
    @Autowired
    public ReservationService(ReservationRepository reservationRepository, TableRepository tableRepository, UserRRepository userRRepository, Restaurant restaurant) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
        this.userRRepository = userRRepository;
        this.restaurant = restaurant;
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
        // Check if the user exists in the database
        Optional<UserR> optionalUser = userRRepository.findUserByEmail(user.getEmail());

        UserR managedUserR;
        if (optionalUser.isPresent()) {
            // User exists, use the existing user
            managedUserR = optionalUser.get();
        } else {
            // User does not exist, create a new user
            managedUserR = userRRepository.save(user);
        }
        RestaurantTable managedTable = tableRepository.save(table);
        Reservation reservation = new Reservation(managedUserR, startTime, endTime, managedTable);
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

    public void saveReservation(Reservation reservation){
        reservationRepository.save(reservation);
    }
}

