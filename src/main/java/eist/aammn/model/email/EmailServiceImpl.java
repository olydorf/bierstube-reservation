package eist.aammn.model.email;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender javaMailSender;

    public EmailServiceImpl(@Qualifier("mailSender") JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendReservationConfirmation(String recipientName, String recipientEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("reservation@oly-dorf.de"); // set the "from" address here
        message.setTo(recipientEmail);
        message.setSubject("Reservation Confirmation");
        message.setText("Dear " + recipientName + ",\n\nThank you for making a reservation with us.\n\nWe look forward to seeing you soon!\n\nBest regards,\nBierstube Team");
        javaMailSender.send(message);
    }
}

