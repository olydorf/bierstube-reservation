package eist.aammn.model.dashboard;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import eist.aammn.model.email.EmailService;
import eist.aammn.model.user.Reservation;
import eist.aammn.model.user.ReservationService;

public class DashboardController {

    @Autowired
    private final ReservationService _reservationService;
    
    private final EmailService emailService;

    public DashboardController(ReservationService reservationService, EmailService emailService){
        this._reservationService = reservationService;
        this.emailService = emailService;
    }
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        List<Reservation> reservations = _reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);

        return "dashboard";
    }

    @GetMapping("/confirm-reservation/{id}")
    public String confirmReservation(@PathVariable("id") int id) {
        Optional<Reservation> optionalReservation = _reservationService.getReservationById(id);

        if (optionalReservation.isPresent()) {
            var reservation = optionalReservation.get();
            var customer = reservation.getUser();

            if(reservation.getAmountGuests() > 8){
              emailService.notifyReservationEmail("info@biertube.de", reservation);
              // TODO: add more logic here
            }
            
            reservation.setStatus(true);
            _reservationService.saveReservation(reservation); // TODO: replace void with CompletableFuture ? Check if save successfull

            emailService.sendReservationConfirmation(customer.getName(), customer.getEmail());
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/cancel-reservation/{id}")
    public String cancelReservation(@PathVariable("id") int id) {
        Optional<Reservation> optionalReservation = _reservationService.getReservationById(id);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            
            if(reservation.getStatus()){
                reservation.setStatus(false);
                _reservationService.saveReservation(reservation);
            }
        }

        return "redirect:/dashboard";
    }
}
