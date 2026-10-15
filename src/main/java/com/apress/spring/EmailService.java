package com.apress.spring;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRecoveryEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Remitente: tu cuenta Gmail configurada en application.properties
        helper.setFrom("maxiagueroooo@gmail.com");

        // Destinatario: el email del usuario que pidió recuperar contraseña
        helper.setTo(to);

        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = cuerpo en HTML

        mailSender.send(message);
    }
}
