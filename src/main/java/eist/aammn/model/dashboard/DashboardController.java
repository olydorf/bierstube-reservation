package eist.aammn.model.dashboard;

import eist.aammn.model.email.EmailService;
import eist.aammn.model.user.ReservationService;
import eist.aammn.model.user.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DashboardController {

    @Autowired
    private final ReservationService _reservationService;
    @Autowired
    private final EmailService emailService;
    Logger logger = LoggerFactory.getLogger(DashboardController.class);

    public DashboardController(ReservationService reservationService, EmailService emailService){
        this._reservationService = reservationService;
        this.emailService = emailService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<List<Reservation>> showDashboard(Model model) {
        var reservations = _reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);

        return ResponseEntity.ok(reservations);
    }


    @GetMapping("/confirm-reservation/{id}")
    public CompletableFuture<ResponseEntity<String>> confirmReservation(@PathVariable("id") int id) {
        var optionalReservation = _reservationService.getReservationById(id);

        if (optionalReservation.isPresent()) {
            var reservation = optionalReservation.get();

            if (reservation.getAmountGuests() > 8) {
                return emailService.notifyReservationEmail("info@biertube.de", reservation)
                        .thenApplyAsync(notificationResult -> {
                            // TODO: Add more logic here, if needed
                            return ResponseEntity.ok("Reservation confirmation pending");
                        });
            }

            reservation.setStatus(true);
            _reservationService.saveReservation(reservation);

            return emailService.sendReservationConfirmation(reservation.getName(), reservation.getEmail())
                    .thenApply(confirmationMessage -> {
                        if (confirmationMessage.equals("Reservation confirmed successfully")) {
                            return ResponseEntity.ok(confirmationMessage);
                        } else {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(confirmationMessage);
                        }
                    });
        }

        return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
    }


    @GetMapping("/cancel-reservation/{id}")
    public ResponseEntity<String> cancelReservation(@PathVariable("id") int id) {
        var optionalReservation = _reservationService.getReservationById(id);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();

            if (reservation.getStatus()) {
                reservation.setStatus(false);
                _reservationService.saveReservation(reservation);
                return ResponseEntity.ok("Reservation canceled successfully");
            }
        }
        return ResponseEntity.notFound().build();
    }
}
