package eist.aammn.model.dashboard;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import eist.aammn.model.user.Reservation;
import eist.aammn.model.user.ReservationService;

public class DashboardController {

    @Autowired
    private final ReservationService _reservationService;

    public DashboardController(ReservationService reservationService){
        this._reservationService = reservationService;
    }
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        List<Reservation> reservations = _reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);

        return "dashboard";
    }

    @GetMapping("/confirmReservation/{id}")
    public String confirmReservation(@PathVariable("id") int id) {
        Optional<Reservation> optionalReservation = _reservationService.getReservationById(id);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            
            reservation.setStatus(true);
            _reservationService.saveReservation(reservation);
        }

        return "redirect:/dashboard";
    }
}
