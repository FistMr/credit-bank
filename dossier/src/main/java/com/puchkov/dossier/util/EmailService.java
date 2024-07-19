package com.puchkov.dossier.util;

import com.puchkov.dossier.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendSimpleEmail(EmailMessage emailMessage) {
        log.debug("EmailService: sendSimpleEmail(Entrance) parameters: emailMessage = {}", emailMessage);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailMessage.getAddress());
        message.setSubject(emailMessage.getTheme().getTopic());
        message.setText(emailMessage.getTheme().getTopic());
        message.setFrom("bankcredit309@gmail.com");
        mailSender.send(message);
        log.debug("EmailService: sendSimpleEmail(Exit) message has been sent");
    }
}
