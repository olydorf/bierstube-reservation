package eist.aammn.model.email;

public interface EmailService {
    void sendReservationConfirmation(String recipientName, String recipientEmail);
}
