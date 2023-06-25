package eist.aammn.model.email;

import eist.aammn.model.dashboard.DashboardController;
import eist.aammn.model.user.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender javaMailSender;
    private static final String FROM_EMAIL_ADRESS = "reservation@oly-dorf.de";

    private final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    public EmailServiceImpl(@Qualifier("mailSender") JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public CompletableFuture<String> sendReservationConfirmation(String recipientName, String recipientEmail) {
        return CompletableFuture.supplyAsync(() -> {
             try{
                 var message = new SimpleMailMessage();
                 message.setFrom(FROM_EMAIL_ADRESS); // set the "from" address here
                 message.setTo(recipientEmail);
                 message.setSubject("Reservation Confirmation");
                 message.setText("Dear " + recipientName + ",\n\nThank you for making a reservation with us.\n\nWe look forward to seeing you soon!\n\nBest regards,\nBierstube Team");

                 javaMailSender.send(message);

                 return "Notification email sent";

            }catch (Exception e) {
                 return "Error sending reservation confirmation email: " + e.getMessage();
            }

        });
    }

    @Async
    @Override
    public CompletableFuture<String> notifyReservationEmail(String recipientName, Reservation reservation) {
        var message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL_ADRESS);
        message.setTo(recipientName);
        message.setSubject("Reservation Confirmation Notification");
        message.setText("Dear Worker,\n\nYou have a reservation confirmation request from customer " + reservation.getEmail() +
         "\n\nPlease confirm the reservation as soon as possible.\n\nBest regards,\n Reservation Platform");

        javaMailSender.send(message);

        return CompletableFuture.completedFuture("Notification email sent");
    }

    @Async
    @Override
    public CompletableFuture<String> sendPasswordResetEmail(String userEmail, String newPassword) {
        try {
            Thread.sleep(2000); // Simulating a delay of 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL_ADRESS); 
        message.setTo(userEmail);
        message.setSubject("Password Reset Bierstube Reservation Platform");
        message.setText("Dear User,\n\nYour password has been successfully reset to "+ newPassword +" \n\nPlease use the new password to log in.\n\nBest regards,\nYour App Team");
        javaMailSender.send(message);

        return CompletableFuture.completedFuture("Password reset email sent");
    }
}
