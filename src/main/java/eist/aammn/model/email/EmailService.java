package eist.aammn.model.email;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    void sendReservationConfirmation(String recipientName, String recipientEmail);

    CompletableFuture<String> resetPassword(String userEmail);
}
