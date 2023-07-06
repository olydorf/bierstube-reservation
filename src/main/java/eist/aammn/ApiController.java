package eist.aammn;

import eist.aammn.model.email.EmailServiceImpl;
import eist.aammn.model.email.ReservationConfirmationRequest;
import eist.aammn.model.restaurant.Restaurant;
import eist.aammn.model.restaurant.RestaurantDTO;
import eist.aammn.model.restaurant.RestaurantTable;
import eist.aammn.model.user.ReservationService;
import eist.aammn.model.user.model.Reservation;
import eist.aammn.model.user.model.ReservationRequestDTO;
import eist.aammn.model.user.model.UserR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@ResponseBody
@RequestMapping("/api/")
public class ApiController {

    @Autowired
    private Restaurant restaurant;
    @Autowired
    private final ReservationService reservationService;

    @GetMapping("restaurant")
    public ResponseEntity<RestaurantDTO> getRestaurantDetails() {
        return ResponseEntity.ok(restaurant.toDTO());
    }


    @GetMapping("reservations/{id}")
    public ResponseEntity<Reservation> reservation(@PathVariable int id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("reservations")
    public ResponseEntity<List<Reservation>> reservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        if (reservations.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(reservations);
        }    }

    public ApiController(ReservationService reservationService) {
        this.reservationService = reservationService;
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


    @Autowired
    private EmailServiceImpl emailService;

    @PostMapping("/send-confirmation")
    public ResponseEntity<Void> sendReservationConfirmation(@RequestBody ReservationConfirmationRequest request) {
        emailService.sendReservationConfirmation(request.getRecipientName(), request.getRecipientEmail());
        return ResponseEntity.ok().build();
    }



    @GetMapping("tables/freeAt/{time}")
    public ResponseEntity<Set<Integer>> tablesFreeAt(@PathVariable("time") String timeStr) {
        LocalDateTime time = LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_DATE_TIME);
        Set<Integer> freeTableNumbers = reservationService.getFreeTables(time).stream()
                .map(RestaurantTable::getId)
                .collect(Collectors.toSet());

        if (freeTableNumbers.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(freeTableNumbers);
        }
    }





    @GetMapping(path = "reservations/{id}/calendar.ics", produces = ICSBuilder.MIME_TYPE)
    public ResponseEntity<String> reservationCalendar(@PathVariable int id) {
        return reservationService.getReservationById(id)
                .map(Reservation::toICS)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }




    @PostMapping("/reservations")
    public ResponseEntity<Reservation> addReservation(@RequestBody ReservationRequestDTO reservationDTO) {
        try {
            String name = reservationDTO.getName();
            String email = reservationDTO.getEmail();
            int amountGuests = reservationDTO.getAmountGuests();
            LocalDateTime startTime = reservationDTO.getStartTime();
            LocalDateTime endTime = reservationDTO.getEndTime();

            RestaurantTable table = reservationService.assignTableToReservation(startTime, amountGuests);

            if (table == null) {
                if (amountGuests > 8){
                    Reservation reservation = reservationService.createReservation(name, email, amountGuests, startTime, endTime, table);
                    emailService.notifyReservationEmail(name,reservation);
                    return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
                    // email service send an email
                }
                // No table available
                return ResponseEntity.noContent().build(); // Returns a ResponseEntity with no content
            }


            Reservation reservation = reservationService.createReservation(name, email, amountGuests, startTime, endTime, table);
            reservation.setStatus(true);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
