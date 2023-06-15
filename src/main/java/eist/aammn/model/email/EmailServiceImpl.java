package eist.aammn.model.email;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import eist.aammn.model.user.Reservation;

@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender javaMailSender;
    private static final String FROM_EMAIL_ADRESS = "reservation@oly-dorf.de";

    public EmailServiceImpl(@Qualifier("mailSender") JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendReservationConfirmation(String recipientName, String recipientEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL_ADRESS); // set the "from" address here
        message.setTo(recipientEmail);
        message.setSubject("Reservation Confirmation");
        message.setText("Dear " + recipientName + ",\n\nThank you for making a reservation with us.\n\nWe look forward to seeing you soon!\n\nBest regards,\nBierstube Team");
        javaMailSender.send(message);
    }

    @Async
    @Override
    public CompletableFuture<String> notifyReservationEmail(String recipientName, Reservation reservation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL_ADRESS);
        message.setTo(recipientName);
        message.setSubject("Reservation Confirmation Notification");
        message.setText("Dear Worker,\n\nYou have a reservation confirmation request from customer " + reservation.getUser().getEmail() +
         "\n\nPlease confirm the reservation as soon as possible.\n\nBest regards,\n Reservation Platform");

        javaMailSender.send(message);

        return CompletableFuture.completedFuture("Notification email sent");
    }

    @Async
    @Override
    public CompletableFuture<String> sendPasswordResetEmail(String userEmail, String newPassword) {

        //TODO: Check against DB, change password and send the new one in the email. Hash?

        try {
            Thread.sleep(2000); // Simulating a delay of 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL_ADRESS); 
        message.setTo(userEmail);
        message.setSubject("Password Reset Bierstube Reservation Platform");
        message.setText("Dear User,\n\nYour password has been successfully reset.\n\nPlease use the new password to log in.\n\nBest regards,\nYour App Team");
        javaMailSender.send(message);

        return CompletableFuture.completedFuture("Password reset email sent");
    }
}
