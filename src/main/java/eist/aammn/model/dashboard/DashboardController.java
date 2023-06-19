package eist.aammn.model.dashboard;

import eist.aammn.model.email.EmailService;
import eist.aammn.model.user.ReservationService;
import eist.aammn.model.user.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public class DashboardController {

    @Autowired
    private final ReservationService _reservationService;
    
    private final EmailService emailService;

    
    Logger logger = LoggerFactory.getLogger(DashboardController.class);

    public DashboardController(ReservationService reservationService, EmailService emailService){
        this._reservationService = reservationService;
        this.emailService = emailService;
    }
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        var reservations = _reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);

        return "dashboard";
    }

    @GetMapping("/confirm-reservation/{id}")
    public String confirmReservation(@PathVariable("id") int id) {
        var optionalReservation = _reservationService.getReservationById(id);

        if (optionalReservation.isPresent()) {
            var reservation = optionalReservation.get();
            var customer = reservation.getUser();

            if(reservation.getAmountGuests() > 8){
              emailService.notifyReservationEmail("info@biertube.de", reservation);
              // TODO: add more logic here, probably in Dashboard Service
            }
            reservation.setStatus(true);
            _reservationService.saveReservation(reservation); // TODO: replace void with CompletableFuture ? Check if save successfull

            emailService.sendReservationConfirmation(customer.getUsername(), customer.getEmail());
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/cancel-reservation/{id}")
    public String cancelReservation(@PathVariable("id") int id) {
        var optionalReservation = _reservationService.getReservationById(id);

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
