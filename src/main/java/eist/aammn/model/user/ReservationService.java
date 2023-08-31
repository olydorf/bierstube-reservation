package eist.aammn.model.user;

import eist.aammn.model.restaurant.Restaurant;
import eist.aammn.model.restaurant.RestaurantTable;
import eist.aammn.model.user.model.Reservation;
import eist.aammn.model.user.model.UserR;
import eist.aammn.model.user.repository.ReservationRepository;
import eist.aammn.model.user.repository.TableRepository;
import eist.aammn.model.user.repository.UserRRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private  final TableRepository tableRepository;


    @Autowired
    private Restaurant restaurant;
    @Autowired
    public ReservationService(ReservationRepository reservationRepository, TableRepository tableRepository, UserRRepository userRRepository, Restaurant restaurant) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
        this.restaurant = restaurant;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Integer id) {
        return reservationRepository.findById(id);
    }

    public Reservation createReservation(String name,String email,int amountGuests, LocalDateTime startTime, LocalDateTime endTime, RestaurantTable table) {

        



        RestaurantTable managedTable = tableRepository.save(table);
        Reservation reservation = new Reservation(name, email, startTime, endTime, managedTable, amountGuests);

                 return reservationRepository.save(reservation);
    }
    public int getTotalGuestsForDay(LocalDateTime time) {
        List<Reservation> reservationsForDay = getAllReservations().stream()
                .filter(r -> r.getStartTime().toLocalDate().equals(time.toLocalDate()))
                .collect(Collectors.toList());

        return reservationsForDay.stream().mapToInt(Reservation::getAmountGuests).sum();
    }

    public Set<RestaurantTable> getFreeTables(LocalDateTime time) {
        int alreadyReservedGuests = getTotalGuestsForDay(time);

        if (alreadyReservedGuests >= 16) {
            // All tables are occupied if the daily limit is reached
            return new HashSet<>();
        }

        return Restaurant.getTables().stream().collect(Collectors.toSet());
    }
    public RestaurantTable assignTableToReservation(LocalDateTime time, int amountGuests) {
        int alreadyReservedGuests = getTotalGuestsForDay(time);
        if (alreadyReservedGuests + amountGuests > 16) {
            // Daily limit exceeded
            return null;
        }
        Set<RestaurantTable> freeTables = getFreeTables(time);

        // Sort tables by capacity in ascending order
        List<RestaurantTable> sortedTables = freeTables.stream()
                .sorted(Comparator.comparingInt(RestaurantTable::getCapacity))
                .toList();

        for (RestaurantTable table : sortedTables) {
            if (table.getCapacity() >= amountGuests) {
                return table;  // return the assigned table
            }
        }

        // No suitable table found
        return null;
    }


    public void deleteReservation(Integer id) {
        reservationRepository.deleteById(id);
    }

    public void saveReservation(Reservation reservation){
        reservationRepository.save(reservation);
    }
}

