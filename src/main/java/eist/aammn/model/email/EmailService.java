package eist.aammn.model.email;

import eist.aammn.model.user.model.Reservation;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<String> sendReservationConfirmation(String recipientName, String recipientEmail);

    CompletableFuture<String> sendPasswordResetEmail(String userEmail, String newPassword);

    CompletableFuture<String> notifyReservationEmail(String workerEmail, Reservation reservation);
}
