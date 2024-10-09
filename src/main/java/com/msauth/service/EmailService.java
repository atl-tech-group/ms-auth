package com.msauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String projectEmail;

    public void sendBirthdayEmail(String email) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(projectEmail);
        message.setTo(email);
        message.setSubject("Happy Birthday!");
        message.setText("Happy birthday :) ");

        javaMailSender.send(message);
    }
}
