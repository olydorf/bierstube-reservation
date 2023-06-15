package eist.aammn.model.email;

import java.util.concurrent.CompletableFuture;

import eist.aammn.model.user.Reservation;

public interface EmailService {
    void sendReservationConfirmation(String recipientName, String recipientEmail);

    CompletableFuture<String> sendPasswordResetEmail(String userEmail, String newPassword);

    CompletableFuture<String> notifyReservationEmail(String workerEmail, Reservation reservation);
}
