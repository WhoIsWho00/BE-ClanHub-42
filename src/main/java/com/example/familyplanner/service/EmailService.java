package com.example.familyplanner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

       public void sendPasswordResetEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Family Planner - Password Reset Code");
        message.setText("Your password reset code is: " + code +
                "\n\nThis code will expire in 5 minutes. If you did not request a password reset, please ignore this message.");

        mailSender.send(message);
    }

    public void sendConfirmationEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Family Planner - Account Registration");
        message.setText("Hello " + username + ",\n\n" +
                "Thank you for registering with Family Planner. Your account has been successfully created.\n\n" +
                "You can now log in using your email and password.\n\n" +
                "Best regards,\nFamily Planner Team");

        mailSender.send(message);
    }
}