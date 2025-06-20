package login_registracija_aplikacija.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendContactFormEmail(String fromName, String fromEmailAddress, String subject, String messageContent, String recipientEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(recipientEmail);
        message.setSubject("WinxCare Kontakt Forma: " + subject);
        message.setText("Primili ste poruku iz kontakt forme WinxCare web stranice:\n\n"
                + "Ime: " + fromName + "\n"
                + "Email: " + fromEmailAddress + "\n"
                + "Naslov: " + subject + "\n\n"
                + "Poruka:\n" + messageContent + "\n\n"
                + "Odgovorite direktno na email: " + fromEmailAddress);
        message.setReplyTo(fromEmailAddress);
        mailSender.send(message);
    }
}
