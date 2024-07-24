package com.puchkov.dossier.util;

import com.puchkov.dossier.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendSimpleEmail(EmailMessage emailMessage) {
        sendSimpleEmail(emailMessage.getAddress(), emailMessage.getTheme().getTopic(), emailMessage.getTheme().getTopic());
    }

    public void sendSimpleEmail(String to, String subject, String text) {
        log.debug("EmailService: sendSimpleEmail(Entrance) parameters: to = {}, subject = {}, text = {}", to, subject, text);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("puchokok2001@gmail.com");
        try {
            mailSender.send(message);
        } catch (Exception ex) {
            log.debug("Error send email message");
        }
        log.debug("EmailService: sendSimpleEmail(Exit) message has been sent");
    }

    public void sendMessageWithDocuments(String to, String subject, String text, ByteArrayResource resource) throws MessagingException, javax.mail.MessagingException {
        log.debug("EmailService: sendMessageWithDocuments(Entrance) parameters: to = {}, subject = {}, text = {}, resource = {}", to, subject, text, resource);
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("puchokok2001@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        helper.addAttachment("document.docx", resource);

        mailSender.send(message);
        log.debug("EmailService: sendMessageWithDocuments(Exit) message has been sent");
    }
}
