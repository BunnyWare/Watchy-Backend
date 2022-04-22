package de.fayedev.watchybackend.service;

import de.fayedev.watchybackend.model.common.Email;
import de.fayedev.watchybackend.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    @Value("${mailing.from}")
    private String from;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(Email email) {
        var message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email.getTo());
        message.setSubject(email.getSubject());
        message.setText(email.getText());

        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.warn(LogMessage.MAIL_ERROR, email.getTo(), email.getSubject(), e);
        }
    }

}
